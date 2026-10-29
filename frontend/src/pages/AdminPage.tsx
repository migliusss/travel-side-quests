import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ApiError, api } from '../api/client'
import type { SideQuestStatus } from '../api/types'

const STATUSES: SideQuestStatus[] = ['PENDING', 'APPROVED', 'REJECTED']

export default function AdminPage() {
  // Kept in component state only: nothing about this is remembered between reloads.
  const [credentials, setCredentials] = useState<string | null>(null)
  const [user, setUser] = useState('admin')
  const [password, setPassword] = useState('')
  const [status, setStatus] = useState<SideQuestStatus>('PENDING')

  if (!credentials) {
    return (
      <SignIn
        user={user}
        password={password}
        onUser={setUser}
        onPassword={setPassword}
        onSubmit={() => setCredentials(`${user}:${password}`)}
      />
    )
  }

  return (
    <Queue
      credentials={credentials}
      status={status}
      onStatus={setStatus}
      onSignOut={() => {
        setCredentials(null)
        setPassword('')
      }}
    />
  )
}

function SignIn({
  user,
  password,
  onUser,
  onPassword,
  onSubmit,
}: {
  user: string
  password: string
  onUser: (value: string) => void
  onPassword: (value: string) => void
  onSubmit: () => void
}) {
  function handle(event: FormEvent) {
    event.preventDefault()
    onSubmit()
  }

  return (
    <form
      onSubmit={handle}
      className="mx-auto max-w-sm space-y-4 rounded-2xl border border-stone/70 bg-white p-6"
    >
      <h1 className="font-display text-xl font-bold">Review queue</h1>
      <p className="text-sm text-oxblood/60">The moderation account set in the backend.</p>

      <div>
        <label className="label" htmlFor="user">
          User
        </label>
        <input id="user" className="field" value={user} onChange={(e) => onUser(e.target.value)} />
      </div>

      <div>
        <label className="label" htmlFor="password">
          Password
        </label>
        <input
          id="password"
          type="password"
          className="field"
          value={password}
          onChange={(e) => onPassword(e.target.value)}
        />
      </div>

      <button type="submit" className="btn-primary w-full">
        Open the queue
      </button>
    </form>
  )
}

function Queue({
  credentials,
  status,
  onStatus,
  onSignOut,
}: {
  credentials: string
  status: SideQuestStatus
  onStatus: (value: SideQuestStatus) => void
  onSignOut: () => void
}) {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)

  const queue = useQuery({
    queryKey: ['moderation', status, page],
    queryFn: () => api.moderationQueue(credentials, status, page),
    retry: false,
  })

  function invalidate() {
    queryClient.invalidateQueries({ queryKey: ['moderation'] })
    queryClient.invalidateQueries({ queryKey: ['sideQuests'] })
    queryClient.invalidateQueries({ queryKey: ['countries'] })
    queryClient.invalidateQueries({ queryKey: ['destinations'] })
  }

  const approve = useMutation({
    mutationFn: (id: number) => api.approve(credentials, id),
    onSuccess: invalidate,
  })

  const reject = useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) =>
      api.reject(credentials, id, reason),
    onSuccess: invalidate,
  })

  if (queue.isError && (queue.error as ApiError).status === 401) {
    return (
      <div className="mx-auto max-w-sm space-y-4 rounded-2xl bg-wine-50 p-6 text-center">
        <p className="text-sm text-wine-700">Those credentials were not accepted.</p>
        <button type="button" className="btn-ghost" onClick={onSignOut}>
          Try again
        </button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center gap-3">
        <h1 className="font-display text-2xl font-bold">Review queue</h1>

        <div className="ml-auto flex gap-1">
          {STATUSES.map((entry) => (
            <button
              key={entry}
              type="button"
              onClick={() => {
                onStatus(entry)
                setPage(0)
              }}
              className={`rounded-full px-3 py-1 text-xs font-semibold transition ${
                status === entry
                  ? 'bg-wine-600 text-cream'
                  : 'bg-white text-oxblood/60 hover:text-wine-600'
              }`}
            >
              {entry.toLowerCase()}
            </button>
          ))}
        </div>

        <button type="button" className="btn-ghost" onClick={onSignOut}>
          Sign out
        </button>
      </div>

      {queue.isPending && <p className="text-oxblood/60">Loading…</p>}

      {queue.data?.content.length === 0 && (
        <div className="rounded-2xl border border-dashed border-stone p-10 text-center text-oxblood/60">
          Nothing {status.toLowerCase()}.
        </div>
      )}

      <ul className="space-y-4">
        {queue.data?.content.map((quest) => (
          <li
            key={quest.id}
            className="flex flex-col gap-4 rounded-2xl border border-stone/70 bg-white p-4 sm:flex-row"
          >
            {quest.images[0] && (
              <img
                src={quest.images[0].imageUrl}
                alt={quest.images[0].altText ?? ''}
                className="h-32 w-full rounded-xl object-cover sm:w-44"
              />
            )}

            <div className="flex-1 space-y-1">
              <p className="text-xs font-semibold tracking-wide text-placid-700 uppercase">
                {quest.destination.name}, {quest.destination.country.name}
              </p>
              <h2 className="font-display text-lg font-semibold">{quest.name}</h2>
              <p className="text-sm text-oxblood/70">{quest.description}</p>
              <p className="text-xs text-oxblood/40">
                {quest.categories.map((c) => c.name.toLowerCase()).join(' · ')}
                {quest.submittedBy ? ` — credited to ${quest.submittedBy}` : ''}
              </p>
              {quest.status === 'APPROVED' && (
                <Link
                  to={`/side-quests/${quest.id}`}
                  className="inline-block text-xs font-semibold text-wine-600 hover:underline"
                >
                  View it live
                </Link>
              )}
            </div>

            <div className="flex gap-2 sm:flex-col">
              <button
                type="button"
                className="btn-primary"
                disabled={quest.status === 'APPROVED' || approve.isPending}
                onClick={() => approve.mutate(quest.id)}
              >
                Approve
              </button>
              <button
                type="button"
                className="btn-ghost"
                disabled={quest.status === 'REJECTED' || reject.isPending}
                onClick={() => {
                  const reason = window.prompt('Why is this being rejected?') ?? ''
                  reject.mutate({ id: quest.id, reason })
                }}
              >
                Reject
              </button>
            </div>
          </li>
        ))}
      </ul>

      {queue.data && queue.data.totalPages > 1 && (
        <div className="flex items-center justify-center gap-3">
          <button
            type="button"
            className="btn-ghost"
            disabled={page === 0}
            onClick={() => setPage((current) => current - 1)}
          >
            Previous
          </button>
          <span className="text-sm text-oxblood/60">
            {queue.data.page + 1} of {queue.data.totalPages}
          </span>
          <button
            type="button"
            className="btn-ghost"
            disabled={!queue.data.hasNext}
            onClick={() => setPage((current) => current + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  )
}
