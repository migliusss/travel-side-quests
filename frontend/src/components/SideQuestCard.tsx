import { Link } from 'react-router-dom'
import type { SideQuest } from '../api/types'

export default function SideQuestCard({ quest }: { quest: SideQuest }) {
  const cover = quest.images[0]

  return (
    <Link
      to={`/side-quests/${quest.id}`}
      className="group flex flex-col overflow-hidden rounded-2xl border border-sage-200 bg-white
                 transition hover:-translate-y-0.5 hover:border-pink-300 hover:shadow-lg"
    >
      <div className="aspect-4/3 overflow-hidden bg-lime-100">
        {cover ? (
          <img
            src={cover.imageUrl}
            alt={cover.altText ?? quest.name}
            loading="lazy"
            className="h-full w-full object-cover transition duration-500 group-hover:scale-105"
          />
        ) : (
          <div className="flex h-full items-center justify-center text-sm text-forest-700">
            No photo yet
          </div>
        )}
      </div>

      <div className="flex flex-1 flex-col gap-2 p-4">
        <p className="text-xs font-semibold tracking-wide text-forest-700 uppercase">
          {quest.destination.name}, {quest.destination.country.name}
        </p>

        <h3 className="font-display text-lg leading-snug font-semibold">{quest.name}</h3>

        <p className="line-clamp-3 text-sm text-forest-700">{quest.description}</p>

        <div className="mt-auto flex flex-wrap gap-1.5 pt-2">
          {quest.categories.map((category) => (
            <span
              key={category.id}
              className="rounded-full bg-lime-100 px-2 py-0.5 text-[11px] font-medium
                         tracking-wide text-forest-700"
            >
              {category.name.toLowerCase()}
            </span>
          ))}
        </div>
      </div>
    </Link>
  )
}
