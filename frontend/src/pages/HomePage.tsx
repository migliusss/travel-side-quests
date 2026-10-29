import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api/client'
import CategoryIcon from '../components/CategoryIcon'
import HeroBackdrop from '../components/HeroBackdrop'
import SideQuestCard from '../components/SideQuestCard'

export default function HomePage() {
  const navigate = useNavigate()
  const [search, setSearch] = useState('')

  const categories = useQuery({ queryKey: ['categories'], queryFn: api.categories })

  const latest = useQuery({
    queryKey: ['sideQuests', 'latest'],
    queryFn: () => api.sideQuests({}, 0, 3),
  })

  function onSearch(event: FormEvent) {
    event.preventDefault()

    const query = search.trim()
    navigate(query ? `/browse?q=${encodeURIComponent(query)}` : '/browse')
  }

  return (
    <>
      <section className="relative isolate overflow-hidden">
        <HeroBackdrop />

        <div className="relative mx-auto max-w-3xl px-5 py-24 text-center sm:py-32">
          <h1 className="font-display text-4xl leading-tight font-bold text-cream sm:text-5xl">
            The places that never make the top ten
          </h1>

          <p className="mx-auto mt-4 max-w-xl text-cream/75">
            Detours worth taking, each one recommended by someone who went.
          </p>

          <form onSubmit={onSearch} className="mt-9">
            <label htmlFor="search" className="sr-only">
              Search for a place
            </label>

            <div
              className="flex items-center gap-2 rounded-full bg-white p-2 pl-5 shadow-xl
                         ring-1 ring-black/5 focus-within:ring-2 focus-within:ring-spinach-500"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth={2}
                strokeLinecap="round"
                aria-hidden="true"
                className="h-5 w-5 shrink-0 text-wine-600"
              >
                <circle cx="11" cy="11" r="7" />
                <path d="M20 20l-3.5-3.5" />
              </svg>

              <input
                id="search"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Where do you want to go?"
                className="min-w-0 flex-1 bg-transparent py-2 text-[15px] outline-none
                           placeholder:text-oxblood/40"
              />

              <button type="submit" className="btn-primary shrink-0 rounded-full px-5">
                Search
              </button>
            </div>
          </form>

          <p className="mt-4 text-sm text-cream/60">
            Know somewhere?{' '}
            <Link to="/submit" className="font-semibold text-cream underline underline-offset-4">
              Add it
            </Link>
          </p>
        </div>
      </section>

      <section className="mx-auto max-w-5xl px-5 py-14 text-center">
        <h2 className="font-display text-2xl font-bold">Find your next…</h2>

        <ul className="mt-8 flex flex-wrap justify-center gap-x-4 gap-y-8 sm:gap-x-5">
          {categories.data?.map((category) => (
            <li key={category.id}>
              <Link
                to={`/browse?categoryIds=${category.id}`}
                className="group flex w-20 flex-col items-center gap-2"
              >
                <span
                  className="flex h-16 w-16 items-center justify-center rounded-full
                             bg-spinach-100 text-spinach-700 transition
                             group-hover:bg-spinach-500 group-hover:text-cream"
                >
                  <CategoryIcon name={category.name} className="h-7 w-7" />
                </span>
                <span className="text-xs font-semibold tracking-wide text-spinach-700 uppercase">
                  {category.name.toLowerCase()}
                </span>
              </Link>
            </li>
          ))}
        </ul>
      </section>

      {latest.data && latest.data.content.length > 0 && (
        <section className="mx-auto max-w-6xl px-5 pb-20">
          <div className="mb-5 flex items-baseline justify-between">
            <h2 className="font-display text-2xl font-bold">Recently added</h2>
            <Link to="/browse" className="text-sm font-semibold text-wine-600 hover:underline">
              See all {latest.data.totalElements}
            </Link>
          </div>

          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
            {latest.data.content.map((quest) => (
              <SideQuestCard key={quest.id} quest={quest} />
            ))}
          </div>
        </section>
      )}
    </>
  )
}
