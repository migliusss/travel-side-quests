/**
 * Stands in for the photograph a travel site would normally put here: layered ridgelines
 * in the palette, so the hero works before anyone has uploaded anything and we are not
 * shipping someone else's picture.
 *
 * The pink sits where a dawn would be — low, behind the ridges — which is the one place
 * a pastel reads as light rather than as a sticker.
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
          <stop offset="0%" stopColor="#141a0e" />
          <stop offset="62%" stopColor="#28301c" />
          <stop offset="100%" stopColor="#3e4a2c" />
        </linearGradient>
        <linearGradient id="hero-dawn" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#ffc3cc" stopOpacity="0" />
          <stop offset="60%" stopColor="#ffc3cc" stopOpacity="0.34" />
          <stop offset="100%" stopColor="#f5a0b1" stopOpacity="0.6" />
        </linearGradient>
        <linearGradient id="hero-far" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#d2db76" stopOpacity="0.34" />
          <stop offset="100%" stopColor="#d2db76" stopOpacity="0.1" />
        </linearGradient>
      </defs>

      <rect width="1440" height="480" fill="url(#hero-sky)" />
      <circle cx="1180" cy="92" r="44" fill="#eef2d0" opacity="0.14" />
      {/* Sits just above the far ridge — the band of sky the crop actually shows. */}
      <rect y="150" width="1440" height="165" fill="url(#hero-dawn)" />

      {/* Far range: lime, hazy */}
      <path
        fill="url(#hero-far)"
        d="M0 352 C 150 300, 250 250, 400 282 S 640 350, 780 300 S 1040 232, 1200 288 S 1370 338, 1440 312 L1440 480 L0 480 Z"
      />

      {/* Middle range */}
      <path
        fill="#3e4a2c"
        opacity="0.88"
        d="M0 404 C 180 356, 300 330, 452 364 S 700 428, 868 374 S 1130 318, 1290 366 S 1400 396, 1440 386 L1440 480 L0 480 Z"
      />

      {/* Near treeline, almost a silhouette */}
      <path
        fill="#141a0e"
        opacity="0.94"
        d="M0 446 C 160 418, 300 408, 470 432 S 760 470, 940 438 S 1230 404, 1440 436 L1440 480 L0 480 Z"
      />
    </svg>
  )
}
