// Mirrors the DTOs in com.example.travelsidequests.dto.
//
// Hand-written. Generating them from /v3/api-docs with openapi-typescript would be better,
// but that package still pins TypeScript 5.x and this project is on 7.

export type SideQuestStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export interface Country {
  id: number
  name: string
  countryCode: string
}

export interface Destination {
  id: number
  country: Country
  name: string
  type: string | null
}

export interface Category {
  id: number
  name: string
}

export interface SourceType {
  id: number
  name: string
}

export interface Source {
  id: number
  name: string
  type: SourceType
  author: string | null
  url: string | null
  pageNumber: string | null
}

export interface Image {
  id: number
  imageUrl: string
  altText: string | null
}

export interface SideQuest {
  id: number
  destination: Destination
  name: string
  description: string
  address: string | null
  latitude: number
  longitude: number
  websiteUrl: string | null
  status: SideQuestStatus
  submittedBy: string | null
  createdAt: string
  updatedAt: string
  categories: Category[]
  sources: Source[]
  images: Image[]
}

export interface Page<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  hasNext: boolean
}

export interface SideQuestFilter {
  /** Free text, matched against the name, description, destination and country. */
  q?: string
  countryId?: number
  destinationId?: number
  categoryIds?: number[]
  sourceTypeId?: number
}

export interface ImageSubmission {
  storageKey: string
  altText?: string | null
}

export interface SourceSubmission {
  name: string
  sourceTypeId: number
  url?: string | null
  author?: string | null
  pageNumber?: string | null
}

export interface CreateSideQuest {
  name: string
  description: string
  address?: string | null
  latitude: number
  longitude: number
  websiteUrl?: string | null
  countryCode: string
  destinationId?: number | null
  destinationName?: string | null
  destinationType?: string | null
  categoryIds: number[]
  images: ImageSubmission[]
  sources: SourceSubmission[]
  submittedBy?: string | null
}

/** RFC 7807 problem detail, as returned by GlobalExceptionHandler. */
export interface ProblemDetail {
  title?: string
  detail?: string
  status?: number
  errors?: Record<string, string>
}
