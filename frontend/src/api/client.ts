import type {
  Category,
  Country,
  CreateSideQuest,
  Destination,
  Page,
  ProblemDetail,
  SideQuest,
  SideQuestFilter,
  SideQuestStatus,
  SourceType,
} from './types'

const BASE = '/api/v1'

/** Carries the per-field messages the backend puts in the problem detail. */
export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors: Record<string, string>

  constructor(status: number, message: string, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${BASE}${path}`, {
    ...init,
    headers: {
      ...(init?.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
      ...init?.headers,
    },
  })

  if (!response.ok) {
    const problem = (await response.json().catch(() => ({}))) as ProblemDetail

    throw new ApiError(
      response.status,
      problem.detail ?? problem.title ?? `Request failed (${response.status}).`,
      problem.errors ?? {},
    )
  }

  if (response.status === 204) return undefined as T

  return (await response.json()) as T
}

function query(params: Record<string, unknown>): string {
  const search = new URLSearchParams()

  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null || value === '') continue

    if (Array.isArray(value)) {
      value.forEach((entry) => search.append(key, String(entry)))
    } else {
      search.set(key, String(value))
    }
  }

  const rendered = search.toString()
  return rendered ? `?${rendered}` : ''
}

function basicAuth(credentials: string): HeadersInit {
  return { Authorization: `Basic ${btoa(credentials)}` }
}

export const api = {
  sideQuests(filter: SideQuestFilter, page: number, size = 12) {
    const hasFilter = Object.values(filter).some(
      (value) => value !== undefined && (!Array.isArray(value) || value.length > 0),
    )

    const path = hasFilter ? '/side-quests/filter' : '/side-quests'

    return request<Page<SideQuest>>(`${path}${query({ ...filter, page, size })}`)
  },

  sideQuest(id: number) {
    return request<SideQuest>(`/side-quests/${id}`)
  },

  countries(withQuestsOnly = true) {
    return request<Country[]>(`/countries${query({ withQuestsOnly })}`)
  },

  destinations(countryId?: number) {
    return request<Destination[]>(`/destinations${query({ countryId })}`)
  },

  categories() {
    return request<Category[]>('/categories')
  },

  sourceTypes() {
    return request<SourceType[]>('/source-types')
  },

  uploadImage(file: File) {
    const body = new FormData()
    body.append('file', file)

    return request<{ storageKey: string }>('/images', { method: 'POST', body })
  },

  submit(payload: CreateSideQuest) {
    return request<SideQuest>('/side-quests', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },

  moderationQueue(credentials: string, status: SideQuestStatus, page: number) {
    return request<Page<SideQuest>>(
      `/admin/side-quests${query({ status, page, size: 20 })}`,
      { headers: basicAuth(credentials) },
    )
  },

  approve(credentials: string, id: number) {
    return request<SideQuest>(`/admin/side-quests/${id}/approve`, {
      method: 'POST',
      headers: basicAuth(credentials),
    })
  },

  reject(credentials: string, id: number, reason: string) {
    return request<SideQuest>(`/admin/side-quests/${id}/reject`, {
      method: 'POST',
      headers: basicAuth(credentials),
      body: JSON.stringify({ reason }),
    })
  },
}
