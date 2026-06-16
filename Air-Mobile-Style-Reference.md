# Air Mobile — Style Reference
> sky canvas, frosted glass

**Theme:** light

Air's visual system evokes a serene, cloud-like mobile experience for Android applications. Its typography balances expressive display fonts for impact with highly readable interface text optimized for touch interactions. UI elements remain largely monochromatic, maintaining focus through subtle surface variations and ghost-like components. A single vivid blue accent color is reserved for interactive states, guiding user attention and highlighting primary actions.

# MOBILE PLATFORM TARGET

**Primary Platform:** Android (Material 3 compatible)
**Design Goal:** Premium native Android experience
**Interaction Model:** Touch-first
**Navigation:** Bottom Navigation + Contextual Top App Bar
**Density:** Comfortable
**Motion:** Smooth, subtle, 60fps transitions

---

## COLORS

KEEP ALL EXISTING COLORS EXACTLY AS DEFINED.

- Sky Canvas: #426188
- Action Blue: #2b7fff
- Midnight Ink: #000000
- Cloud White: #ffffff
- Charcoal Text: #1b1b1b
- Haze Grey: #f5f5f5

No additional brand colors allowed.

---

## TYPOGRAPHY

KEEP ALL EXISTING TYPOGRAPHY TOKENS.

Fonts, weights, scales, spacing and hierarchy remain unchanged.

---

## MOBILE LAYOUT

### Screen Structure

1. Status Bar
2. Top App Bar
3. Scrollable Content Area
4. Floating Primary Action (optional)
5. Bottom Navigation

### Screen Width Targets

- Compact: 360–411dp
- Medium: 412–599dp
- Large Tablet: 600dp+

### Safe Areas

Respect:
- Status bar
- Navigation gestures
- Device cutouts
- Rounded screen corners

### Mobile Spacing

Keep existing spacing tokens unchanged.

Use:
- 16px horizontal screen padding
- 20px card padding
- 48px section spacing

---

## MOBILE COMPONENTS

### Top App Bar

Role:
Primary navigation and screen identity.

Background:
Cloud White (#ffffff)

Height:
56dp

Content:
- Back button
- Screen title
- Optional actions

---

### Bottom Navigation

Role:
Primary application navigation.

Background:
Cloud White (#ffffff)

Items:
3–5 maximum

Active State:
Action Blue (#2b7fff)

Inactive State:
Charcoal Text (#1b1b1b)

---

### Floating Action Button

Role:
Primary mobile action.

Background:
Transparent

Border:
1px solid Action Blue (#2b7fff)

Text/Icon:
Action Blue (#2b7fff)

Radius:
9999px

---

### Mobile Feature Card

Background:
Haze Grey (#f5f5f5)

Padding:
20px

Radius:
14px

Shadow:
None

Usage:
Dashboard widgets, summaries, analytics, quick actions.

---

### Mobile Image Card

Background:
Transparent

Radius:
14px

Usage:
Feature previews, reports, charts, profile content.

---

### Mobile Input Field

Background:
Haze Grey (#f5f5f5)

Border:
1px solid rgba(0,0,0,0.1)

Radius:
4px

Optimized for Android keyboards and touch input.

---

## MOBILE UX RULES

### Do

- Design every screen mobile-first.
- Prioritize thumb-friendly interactions.
- Place critical actions within easy reach.
- Use Bottom Navigation for primary app sections.
- Use cards as the main content container.
- Preserve frosted-glass aesthetics.
- Maintain strong contrast.
- Keep interactions fast and lightweight.

### Don't

- Do not use desktop navigation bars.
- Do not use multi-column desktop layouts on phones.
- Do not rely on hover interactions.
- Do not place critical actions near unreachable corners.
- Do not introduce new colors.
- Do not use heavy shadows.
- Do not overload screens with dense information.

---

## MOBILE SURFACES

Level 1:
Sky Canvas (#426188)

Level 2:
Haze Grey (#f5f5f5)

Level 3:
Cloud White (#ffffff)

Unchanged from original system.

---

## MOBILE ELEVATION

The design minimizes shadows.

Depth should be created using:

- Surface separation
- Blur effects
- Frosted glass panels
- Contrast hierarchy

Avoid Material-style heavy elevation.

---

## MOBILE IMAGERY

Use:

- Android device mockups
- Mobile dashboards
- Mobile analytics
- Mobile workflows
- Touch interactions
- Frosted-glass UI previews

Avoid:

- Desktop monitor mockups
- Web browser screenshots
- Multi-window desktop layouts

---

## MOBILE NAVIGATION PATTERN

Primary:
Bottom Navigation

Secondary:
Top App Bar actions

Contextual:
Modal Bottom Sheets

Tertiary:
Drawer only when absolutely necessary.

---

## PREMIUM ANDROID EXPERIENCE

The application should feel comparable to:

- Linear Mobile
- Notion Mobile
- Stripe Dashboard Mobile
- Figma Mobile
- Arc Mobile

Focus on:

- Fluid navigation
- Spacious layouts
- Premium typography
- Frosted glass surfaces
- Minimal visual noise

All original colors, typography, spacing, border radii and brand identity remain unchanged.
