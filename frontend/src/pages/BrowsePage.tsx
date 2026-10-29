import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api/client'
import type { SideQuestFilter } from '../api/types'
import SideQuestCard from '../components/SideQuestCard'

/**
 * The URL is the filter. That keeps the home page's search box and the category links
 * working with nothing but a href, and makes any result list shareable.
 */
export default function BrowsePage() {
  const [params, setParams] = useSearchParams()

  const filter: SideQuestFilter = {
    q: params.get('q') ?? undefined,
    countryId: numeric(params.get('countryId')),
    destinationId: numeric(params.get('destinationId')),
    categoryIds: params.getAll('categoryIds').map(Number).filter(Number.isFinite),
    sourceTypeId: numeric(params.get('sourceTypeId')),
  }

  const page = numeric(params.get('page')) ?? 0

  const [searchText, setSearchText] = useState(filter.q ?? '')

  // The header search and the category links change the URL from outside this component.
  useEffect(() => setSearchText(filter.q ?? ''), [filter.q])

  const countries = useQuery({ queryKey: ['countries'], queryFn: () => api.countries() })
  const categories = useQuery({ queryKey: ['categories'], queryFn: api.categories })
  const sourceTypes = useQuery({ queryKey: ['sourceTypes'], queryFn: api.sourceTypes })

  const destinations = useQuery({
    queryKey: ['destinations', filter.countryId],
    queryFn: () => api.destinations(filter.countryId),
  })

  const quests = useQuery({
    queryKey: ['sideQuests', filter, page],
    queryFn: () => api.sideQuests(filter, page),
  })

  function update(patch: Partial<SideQuestFilter> & { page?: number }) {
    const next = { ...filter, page: 0, ...patch }
    const search = new URLSearchParams()

    if (next.q) search.set('q', next.q)
    if (next.countryId) search.set('countryId', String(next.countryId))
    if (next.destinationId) search.set('destinationId', String(next.destinationId))
    next.categoryIds?.forEach((id) => search.append('categoryIds', String(id)))
    if (next.sourceTypeId) search.set('sourceTypeId', String(next.sourceTypeId))
    if (next.page) search.set('page', String(next.page))

    setParams(search)
  }

  function onSearch(event: FormEvent) {
    event.preventDefault()
    update({ q: searchText.trim() || undefined })
  }

  function toggleCategory(id: number) {
    const selected = filter.categoryIds ?? []

    update({
      categoryIds: selected.includes(id)
        ? selected.filter((entry) => entry !== id)
        : [...selected, id],
    })
  }

  const hasFilter =
    Boolean(filter.q || filter.countryId || filter.destinationId || filter.sourceTypeId) ||
    (filter.categoryIds?.length ?? 0) > 0

  return (
    <div className="grid gap-8 lg:grid-cols-[16rem_1fr]">
      <aside className="space-y-5 lg:sticky lg:top-24 lg:self-start">
        <div className="flex items-center justify-between">
          <h2 className="font-display text-base font-semibold">Narrow it down</h2>
          {hasFilter && (
            <button
              type="button"
              onClick={() => setParams(new URLSearchParams())}
              className="text-xs font-semibold text-wine-600 hover:underline"
            >
              Clear
            </button>
          )}
        </div>

        <form onSubmit={onSearch}>
          <label className="label" htmlFor="search">
            Search
          </label>
          <input
            id="search"
            className="field"
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
            placeholder="Fjords, a town, a country…"
          />
        </form>

        <div>
          <label className="label" htmlFor="country">
            Country
          </label>
          <select
            id="country"
            className="field"
            value={filter.countryId ?? ''}
            onChange={(event) =>
              update({
                countryId: event.target.value ? Number(event.target.value) : undefined,
                destinationId: undefined,
              })
            }
          >
            <option value="">Anywhere</option>
            {countries.data?.map((country) => (
              <option key={country.id} value={country.id}>
                {country.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="label" htmlFor="destination">
            Destination
          </label>
          <select
            id="destination"
            className="field"
            value={filter.destinationId ?? ''}
            onChange={(event) =>
              update({
                destinationId: event.target.value ? Number(event.target.value) : undefined,
              })
            }
          >
            <option value="">Everywhere</option>
            {destinations.data?.map((destination) => (
              <option key={destination.id} value={destination.id}>
                {destination.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <span className="label">Kind of place</span>
          <div className="flex flex-wrap gap-1.5">
            {categories.data?.map((category) => {
              const active = filter.categoryIds?.includes(category.id) ?? false

              return (
                <button
                  key={category.id}
                  type="button"
                  onClick={() => toggleCategory(category.id)}
                  className={`rounded-full px-2.5 py-1 text-xs font-medium transition ${
                    active
                      ? 'bg-spinach-600 text-cream'
                      : 'bg-spinach-50 text-spinach-700 hover:bg-spinach-100'
                  }`}
                >
                  {category.name.toLowerCase()}
                </button>
              )
            })}
          </div>
        </div>

        <div>
          <label className="label" htmlFor="sourceType">
            Heard about it from
          </label>
          <select
            id="sourceType"
            className="field"
            value={filter.sourceTypeId ?? ''}
            onChange={(event) =>
              update({
                sourceTypeId: event.target.value ? Number(event.target.value) : undefined,
              })
            }
          >
            <option value="">Any source</option>
            {sourceTypes.data?.map((type) => (
              <option key={type.id} value={type.id}>
                {type.name.toLowerCase()}
              </option>
            ))}
          </select>
        </div>
      </aside>

      <section>
        <div className="mb-5 flex items-baseline justify-between gap-4">
          <h1 className="font-display text-2xl font-bold">
            {filter.q ? `“${filter.q}”` : 'Side quests'}
          </h1>
          {quests.data && (
            <p className="shrink-0 text-sm text-oxblood/50">
              {quests.data.totalElements} place{quests.data.totalElements === 1 ? '' : 's'}
            </p>
          )}
        </div>

        {quests.isPending && <p className="text-oxblood/60">Loading…</p>}

        {quests.isError && (
          <p className="rounded-lg bg-wine-50 p-4 text-sm text-wine-700">
            Could not load places. Is the backend running?
          </p>
        )}

        {quests.data?.content.length === 0 && (
          <div className="rounded-2xl border border-dashed border-stone p-10 text-center">
            <p className="text-oxblood/60">Nothing matches that yet.</p>
          </div>
        )}

        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
          {quests.data?.content.map((quest) => (
            <SideQuestCard key={quest.id} quest={quest} />
          ))}
        </div>

        {quests.data && quests.data.totalPages > 1 && (
          <div className="mt-8 flex items-center justify-center gap-3">
            <button
              type="button"
              className="btn-ghost"
              disabled={page === 0}
              onClick={() => update({ page: page - 1 })}
            >
              Previous
            </button>
            <span className="text-sm text-oxblood/60">
              {quests.data.page + 1} of {quests.data.totalPages}
            </span>
            <button
              type="button"
              className="btn-ghost"
              disabled={!quests.data.hasNext}
              onClick={() => update({ page: page + 1 })}
            >
              Next
            </button>
          </div>
        )}
      </section>
    </div>
  )
}

function numeric(value: string | null): number | undefined {
  if (!value) return undefined

  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : undefined
}
