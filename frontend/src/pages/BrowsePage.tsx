import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api/client'
import type { SideQuestFilter } from '../api/types'
import SideQuestCard from '../components/SideQuestCard'

export default function BrowsePage() {
  const [filter, setFilter] = useState<SideQuestFilter>({})
  const [page, setPage] = useState(0)

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

  function update(patch: Partial<SideQuestFilter>) {
    setPage(0)
    setFilter((current) => ({ ...current, ...patch }))
  }

  function toggleCategory(id: number) {
    const selected = filter.categoryIds ?? []

    update({
      categoryIds: selected.includes(id)
        ? selected.filter((entry) => entry !== id)
        : [...selected, id],
    })
  }

  const hasFilter = Object.values(filter).some(
    (value) => value !== undefined && (!Array.isArray(value) || value.length > 0),
  )

  return (
    <div className="grid gap-8 lg:grid-cols-[16rem_1fr]">
      <aside className="space-y-5 lg:sticky lg:top-24 lg:self-start">
        <div className="flex items-center justify-between">
          <h2 className="font-display text-base font-semibold">Narrow it down</h2>
          {hasFilter && (
            <button
              type="button"
              onClick={() => {
                setFilter({})
                setPage(0)
              }}
              className="text-xs font-semibold text-wine-600 hover:underline"
            >
              Clear
            </button>
          )}
        </div>

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
        <div className="mb-5 flex items-baseline justify-between">
          <h1 className="font-display text-2xl font-bold">Side quests</h1>
          {quests.data && (
            <p className="text-sm text-oxblood/50">
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
              onClick={() => setPage((current) => current - 1)}
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
              onClick={() => setPage((current) => current + 1)}
            >
              Next
            </button>
          </div>
        )}
      </section>
    </div>
  )
}
