const PATHS: Record<string, string> = {
  HIKING: 'M4 20l5-9 3 4 2-3 6 8M9 11V5m0 0l3 2-3 2',
  NATURE: 'M12 3l5 8h-3l4 6H6l4-6H7l5-8zM12 17v4',
  VIEWPOINT: 'M2 18l6-9 4 5 3-4 7 8H2z M16 6a2 2 0 104 0 2 2 0 10-4 0',
  BEACH: 'M3 19h18M4 19c3-6 9-8 14-7M12 12l7-1M12 12c-1-4 1-7 4-8',
  TOWN: 'M4 21V9l5-4 5 4v12M14 21V12h6v9M7 13h2M7 17h2M17 16h1',
  MUSEUM: 'M3 10l9-6 9 6M5 10v9M10 10v9M14 10v9M19 10v9M3 21h18',
  RESTAURANT: 'M6 3v8a2 2 0 004 0V3M8 11v10M17 3c-1.5 1.5-2 3-2 5s.5 3 2 3v10',
  CAFE: 'M4 8h12v5a5 5 0 01-10 0V8zM16 9h2a2 2 0 010 4h-2M3 21h16',
  HISTORY: 'M7 21V7l5-4 5 4v14M10 21v-5h4v5M12 9v2',
}

const FALLBACK = 'M12 21s7-6.2 7-11a7 7 0 10-14 0c0 4.8 7 11 7 11zM12 8a2 2 0 110 4 2 2 0 010-4'

export default function CategoryIcon({ name, className }: { name: string; className?: string }) {
  return (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={1.6}
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      className={className}
    >
      <path d={PATHS[name.toUpperCase()] ?? FALLBACK} />
    </svg>
  )
}
