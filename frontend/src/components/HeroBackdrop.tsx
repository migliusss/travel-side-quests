/**
 * Stands in for the photograph a travel site would normally put here: layered ridgelines
 * in the palette, so the hero works before anyone has uploaded anything and we are not
 * shipping someone else's picture.
 */
export default function HeroBackdrop() {
  return (
    <svg
      viewBox="0 0 1440 480"
      preserveAspectRatio="xMidYMax slice"
      aria-hidden="true"
      className="absolute inset-0 h-full w-full"
    >
      <defs>
        <linearGradient id="hero-sky" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#2b1217" />
          <stop offset="60%" stopColor="#54202c" />
          <stop offset="100%" stopColor="#7a3242" />
        </linearGradient>
        <linearGradient id="hero-far" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#8cadd3" stopOpacity="0.42" />
          <stop offset="100%" stopColor="#8cadd3" stopOpacity="0.16" />
        </linearGradient>
        <linearGradient id="hero-mid" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#878f50" stopOpacity="0.78" />
          <stop offset="100%" stopColor="#676d3d" stopOpacity="0.55" />
        </linearGradient>
      </defs>

      <rect width="1440" height="480" fill="url(#hero-sky)" />
      <circle cx="1180" cy="96" r="46" fill="#f7f4ea" opacity="0.1" />

      {/* Far range: broad and hazy */}
      <path
        fill="url(#hero-far)"
        d="M0 352 C 150 300, 250 250, 400 282 S 640 350, 780 300 S 1040 232, 1200 288 S 1370 338, 1440 312 L1440 480 L0 480 Z"
      />

      {/* Middle range */}
      <path
        fill="url(#hero-mid)"
        d="M0 404 C 180 356, 300 330, 452 364 S 700 428, 868 374 S 1130 318, 1290 366 S 1400 396, 1440 386 L1440 480 L0 480 Z"
      />

      {/* Near treeline, almost a silhouette */}
      <path
        fill="#231016"
        opacity="0.9"
        d="M0 446 C 160 418, 300 408, 470 432 S 760 470, 940 438 S 1230 404, 1440 436 L1440 480 L0 480 Z"
      />
    </svg>
  )
}
