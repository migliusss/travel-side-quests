import { Link, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api/client'

export default function SideQuestPage() {
  const { id } = useParams()
  const questId = Number(id)

  const quest = useQuery({
    queryKey: ['sideQuest', questId],
    queryFn: () => api.sideQuest(questId),
    enabled: Number.isFinite(questId),
  })

  if (quest.isPending) return <p className="text-oxblood/60">Loading…</p>

  if (quest.isError) {
    return (
      <div className="rounded-2xl border border-dashed border-stone p-10 text-center">
        <p className="text-oxblood/60">That place is not here.</p>
        <Link to="/browse" className="mt-3 inline-block text-sm font-semibold text-wine-600 hover:underline">
          Back to browsing
        </Link>
      </div>
    )
  }

  const place = quest.data

  return (
    <article className="space-y-8">
      <Link to="/browse" className="text-sm font-semibold text-wine-600 hover:underline">
        ← All side quests
      </Link>

      <header className="space-y-2">
        <p className="text-xs font-semibold tracking-wide text-placid-700 uppercase">
          {place.destination.name}, {place.destination.country.name}
        </p>
        <h1 className="font-display text-3xl font-bold">{place.name}</h1>
        {place.submittedBy && (
          <p className="text-sm text-oxblood/50">Added by {place.submittedBy}</p>
        )}
        <div className="flex flex-wrap gap-1.5 pt-1">
          {place.categories.map((category) => (
            <span
              key={category.id}
              className="rounded-full bg-spinach-100 px-2.5 py-0.5 text-xs font-medium text-spinach-700"
            >
              {category.name.toLowerCase()}
            </span>
          ))}
        </div>
      </header>

      {place.images.length > 0 && (
        <div className="grid gap-3 sm:grid-cols-2">
          {place.images.map((image, index) => (
            <img
              key={image.id}
              src={image.imageUrl}
              alt={image.altText ?? place.name}
              // A fixed ratio rather than a max height: the source photos are any shape,
              // and a very wide or very tall one would otherwise set the page height.
              className={`w-full rounded-2xl bg-spinach-100 object-cover ${
                index === 0 ? 'aspect-[16/9] sm:col-span-2' : 'aspect-[4/3]'
              }`}
            />
          ))}
        </div>
      )}

      <div className="grid gap-8 lg:grid-cols-[1fr_18rem]">
        <div className="space-y-4">
          <p className="text-[17px] leading-relaxed whitespace-pre-line">{place.description}</p>

          {place.websiteUrl && (
            <a
              href={place.websiteUrl}
              target="_blank"
              rel="noreferrer noopener"
              className="inline-block text-sm font-semibold text-wine-600 hover:underline"
            >
              Visit the website
            </a>
          )}
        </div>

        <aside className="space-y-6 rounded-2xl border border-stone/70 bg-white p-5">
          {place.address && (
            <div>
              <h2 className="label">Where</h2>
              <p className="text-sm">{place.address}</p>
            </div>
          )}

          <div>
            <h2 className="label">Coordinates</h2>
            <a
              className="text-sm text-placid-700 hover:underline"
              href={`https://www.openstreetmap.org/?mlat=${place.latitude}&mlon=${place.longitude}#map=14/${place.latitude}/${place.longitude}`}
              target="_blank"
              rel="noreferrer noopener"
            >
              {place.latitude}, {place.longitude}
            </a>
          </div>

          <div>
            <h2 className="label">Where this came from</h2>
            {place.sources.length === 0 ? (
              <p className="text-sm text-oxblood/50">Not recorded.</p>
            ) : (
              <ul className="space-y-3">
                {place.sources.map((source) => (
                  <li key={source.id} className="text-sm">
                    <p className="font-semibold">
                      {source.url ? (
                        <a
                          href={source.url}
                          target="_blank"
                          rel="noreferrer noopener"
                          className="text-wine-600 hover:underline"
                        >
                          {source.name}
                        </a>
                      ) : (
                        source.name
                      )}
                    </p>
                    <p className="text-xs text-oxblood/50">
                      {source.type.name.toLowerCase()}
                      {source.author ? ` · ${source.author}` : ''}
                      {source.pageNumber ? ` · p. ${source.pageNumber}` : ''}
                    </p>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </aside>
      </div>
    </article>
  )
}
