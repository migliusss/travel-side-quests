# Travel Side Quests Design System

## Product Direction

Travel Side Quests is a bright, practical, editorial travel directory. The interface should feel
clear and welcoming, with one strong accent for discovery actions and highlighted information.
Avoid heavy decoration; use space, typography, borders, and the accent color to establish hierarchy.

## Color Palette

| Role | Color | Hex | Tailwind usage |
|---|---|---|---|
| Primary accent | Lime | `#A5BE00` | Buttons, active states, focus indicators, important highlights |
| Page background | Soft ivory | `#F6F9E6` | Page and large surface backgrounds |
| Primary text | Ink | `#111300` | Headings, body copy, and text on accent surfaces |
| Surface | White | `#FFFFFF` | Cards, inputs, dialogs, and elevated surfaces |
| Secondary text | Ink 70% | `#535646` | Supporting copy and metadata |
| Muted text | Ink 55% | `#74775F` | Less prominent labels and placeholders |
| Border | Soft sage | `#D9DEC2` | Dividers and input borders |
| Danger | Red | `#B42318` | Error text and destructive actions |
| Success | Deep green | `#3E6600` | Success states and positive status indicators |

### Color Rules

- Use `#A5BE00` as the accent, not as a neutral page tint.
- Use `#111300` for text on the accent color.
- Use `#F6F9E6` for the main page background.
- Use white cards to create a clear but restrained layer above the page.
- Reserve red for genuine errors or destructive actions.
- Do not use low-contrast lime or text tones for essential content.
- Hover states may darken the accent to `#8FA600`; never make accent text lighter than necessary.

## Typography

Roboto is the primary typeface for the complete interface.

- Font family: `Roboto, "Helvetica Neue", Arial, sans-serif`
- Source: [Google Fonts](https://fonts.google.com/specimen/Roboto) or a locally bundled WOFF2
- Antialiasing: enabled

| Style | Size / line height | Weight |
|---|---|---|
| Display | `48 / 52px` desktop, `36 / 40px` mobile | 700 |
| Heading 1 | `36 / 42px` | 700 |
| Heading 2 | `28 / 34px` | 700 |
| Heading 3 | `20 / 26px` | 600 |
| Body | `16 / 24px` | 400 |
| Small / metadata | `14 / 20px` | 400 |
| Label | `12 / 16px` | 600 |

Use font weights consistently:

- 400 for body copy and descriptions.
- 500 for navigation labels and emphasized supporting text.
- 600 for labels, card titles, and button text.
- 700 for page and section headings.
- Avoid using italic as a substitute for hierarchy.

## Spacing

Use an 4px spacing base:

`4, 8, 12, 16, 20, 24, 32, 40, 48, 64`

- Page gutters: 20px on mobile, 32px at larger widths.
- Standard content max width: 1200px.
- Card padding: 20px or 24px.
- Form field height: 44px.
- Button height: 44px.
- Use 24px or 32px gaps between major sections.

## Layout

- Keep the navigation sticky and concise.
- Keep long content in a comfortable reading width, approximately 65–75 characters.
- Cards should use consistent alignment and avoid nested visual treatments.
- Use a responsive two-column or grid layout for place listings when space permits.
- Collapse navigation and action groups into a mobile-friendly layout below 768px.

## Components

### Primary Button

- Background: `#A5BE00`
- Text: `#111300`
- Height: 44px
- Border radius: 8px
- Font: 600
- Padding: 12px 16px
- Hover: `#8FA600`
- Focus: 2px `#A5BE00` outline with a 2px offset
- Disabled: 50% opacity, and the cursor must indicate that the control is unavailable

### Secondary Button

- Background: `#FFFFFF`
- Text: `#111300`
- Border: 1px solid `#D9DEC2`
- Hover: border `#A5BE00` and text `#111300`

### Navigation

- Use `#111300` for primary navigation text.
- Use `#A5BE00` for the active item or indicator.
- Active text may use `#111300` on an accent pill.
- Apply clear hover and focus states without relying on color alone.

### Cards

- Background: `#FFFFFF`
- Border: 1px solid `#D9DEC2`
- Border radius: 12px
- Padding: 20px or 24px
- Use a subtle shadow only for overlays or elevated cards.
- Keep card titles prominent and metadata compact.

### Form Fields

- Height: 44px
- Background: `#FFFFFF`
- Border: 1px solid `#D9DEC2`
- Border radius: 8px
- Placeholder: `#74775F`
- Focus: border `#A5BE00` and 2px translucent accent ring
- Invalid: border `#B42318` and error text directly below the field

### Status and Feedback

- Use icon plus text for errors and success where practical.
- Never communicate status with color alone.
- Toasts and alerts should use an inset border or icon with a concise message.

## Icons

- Use a consistent outline icon set with a 1.5px or 2px stroke.
- Default icon size: 20px.
- Icon-only controls require an accessible label and a visible tooltip or title.
- Use `#111300` as the default icon color and `#A5BE00` for active or highlighted icons.

## Border Radius

- 6px for small controls and tags.
- 8px for buttons and inputs.
- 12px for cards.
- 999px for fully pill-shaped navigation items and compact status labels.

## Motion

- Use restrained transitions of 120–180ms.
- Ease: `ease-out` for entrances and `ease-in-out` for state changes.
- Respect `prefers-reduced-motion: reduce`; disable movement and retain immediate state feedback.

## Accessibility

- Use semantic HTML and a clear heading hierarchy.
- Maintain a visible keyboard focus indicator on every interactive element.
- Do not place light text on the soft ivory or white backgrounds.
- Use `#111300` on `#A5BE00` for accent-filled controls.
- Target WCAG AA contrast for all text and meaningful interface elements.
- Keep touch targets at least 44px in both dimensions.
- Form fields require persistent labels; placeholders cannot replace labels.
