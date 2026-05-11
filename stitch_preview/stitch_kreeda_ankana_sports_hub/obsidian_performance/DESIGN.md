---
name: Obsidian Performance
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1b1b1b'
  surface-container: '#1f1f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353535'
  on-surface: '#e2e2e2'
  on-surface-variant: '#c4c7c8'
  inverse-surface: '#e2e2e2'
  inverse-on-surface: '#303030'
  outline: '#8e9192'
  outline-variant: '#444748'
  surface-tint: '#c6c6c7'
  primary: '#ffffff'
  on-primary: '#2f3131'
  primary-container: '#e2e2e2'
  on-primary-container: '#636565'
  inverse-primary: '#5d5f5f'
  secondary: '#c7c6c6'
  on-secondary: '#2f3131'
  secondary-container: '#484949'
  on-secondary-container: '#b8b8b8'
  tertiary: '#ffffff'
  on-tertiary: '#313030'
  tertiary-container: '#e5e2e1'
  on-tertiary-container: '#656464'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e2e2e2'
  primary-fixed-dim: '#c6c6c7'
  on-primary-fixed: '#1a1c1c'
  on-primary-fixed-variant: '#454747'
  secondary-fixed: '#e3e2e2'
  secondary-fixed-dim: '#c7c6c6'
  on-secondary-fixed: '#1a1c1c'
  on-secondary-fixed-variant: '#464747'
  tertiary-fixed: '#e5e2e1'
  tertiary-fixed-dim: '#c8c6c5'
  on-tertiary-fixed: '#1c1b1b'
  on-tertiary-fixed-variant: '#474746'
  background: '#131313'
  on-background: '#e2e2e2'
  surface-variant: '#353535'
typography:
  display-xl:
    fontFamily: Lexend
    fontSize: 80px
    fontWeight: '900'
    lineHeight: '1.0'
    letterSpacing: -0.04em
  headline-lg:
    fontFamily: Lexend
    fontSize: 48px
    fontWeight: '800'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Lexend
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
  body-lg:
    fontFamily: Lexend
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Lexend
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  label-bold:
    fontFamily: Lexend
    fontSize: 14px
    fontWeight: '700'
    lineHeight: '1.0'
    letterSpacing: 0.1em
  label-sm:
    fontFamily: Lexend
    fontSize: 12px
    fontWeight: '500'
    lineHeight: '1.0'
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 40px
  gutter: 20px
  margin: 32px
---

## Brand & Style

The design system is engineered for elite athletic performance, prioritizing speed, precision, and raw power. The visual identity is defined by a "Dark Mode Only" philosophy, utilizing a high-contrast monochrome palette to eliminate distractions and heighten focus. 

The aesthetic combines **High-Contrast Minimalism** with **Glassmorphism**. It utilizes pure obsidian surfaces and brilliant white accents to create an aggressive, professional atmosphere. Visual depth is achieved through silver gradients that mimic brushed metal and frosted glass overlays that provide a "cool," technical edge. The emotional response is one of intensity, premium quality, and unyielding momentum.

## Colors

This design system operates on an absolute monochrome scale. The foundation is **#000000 (Obsidian Black)**, used for all primary backgrounds and surfaces to create an infinite depth. 

**#FFFFFF (Brilliant White)** serves as the primary action color, used for high-impact text, icons, and critical UI triggers. To introduce a technical, metallic feel, **Subtle Silver Gradients** are applied to secondary surfaces and data visualizations, transitioning from pure white to a mid-tone grey. Glassmorphism is executed using white-tinted translucency with high background blur values to maintain legibility against the deep black void.

## Typography

The typography is the engine of the design system. **Lexend** is used exclusively for its hyper-legible yet athletic structure. 

All headings and display text must be **Italicized and Bold** (weights 700-900) to convey a sense of forward motion and urgency. Tight letter-spacing is applied to larger sizes to create a dense, powerful visual block. Body text remains upright (Normal style) for sustained readability, while labels and utility text utilize uppercase italics to maintain the "high-performance" DNA across small-scale details.

## Layout & Spacing

The layout philosophy follows a **Fixed-Fluid Hybrid Grid**. Content is housed within a rigid 12-column structure for desktop interfaces, ensuring razor-sharp alignment that feels engineered. 

Spacing is governed by a strict 4px baseline power-of-two scale. However, to emphasize the "aggressive" feel, margins are kept generous (#xl) while internal element padding is kept tight (#sm, #md) to create dense clusters of information. This juxtaposition creates a sophisticated, editorial look reminiscent of high-end sports telemetry.

## Elevation & Depth

Depth is not communicated through traditional shadows, but through **Tonal Stacking** and **Refraction**. 

1.  **Level 0 (Base):** Pure #000000.
2.  **Level 1 (Surface):** Subtle dark grey (#1A1A1A) with 1px solid white borders at 10% opacity.
3.  **Level 2 (Glass):** Semi-transparent white overlays (5-10% opacity) with a 20px backdrop blur and 1px "inner glow" white stroke.
4.  **Level 3 (Interaction):** Silver metallic gradients that appear to "lift" off the screen through brightness rather than shadow.

Avoid soft, ambient shadows; if depth must be emphasized, use a hard-edged "offset" stroke or a high-contrast white outer glow to simulate backlighting.

## Shapes

The shape language is strictly **Sharp (0px radius)**. Every container, button, and input field must have 90-degree corners to reinforce the aggressive, professional, and technical nature of the system. 

The only exception to this rule is for purely circular elements (e.g., user avatars or progress rings). All structural UI components must remain rectilinear. To add visual interest to these sharp shapes, use "clipped" corners (45-degree chamfers) on primary action buttons or decorative elements to mimic carbon-fiber or machined-metal components.

## Components

### Buttons
Primary buttons are solid #FFFFFF with #000000 Bold Italic text. Secondary buttons utilize the "Glass" effect: a transparent background with a 2px solid white border and a subtle silver gradient hover state. All buttons must have sharp 0px corners.

### Input Fields
Fields are pure #000000 with a 1px white bottom-border. On focus, the border transitions to a silver gradient and a faint white frosted glass background fills the container. Text is #FFFFFF body-md.

### Cards & Containers
Cards utilize the Level 2 Elevation: white frosted glass with high backdrop blur. Borders are 1px white at 15% opacity. Headers within cards should use the `label-bold` style with a silver gradient text-fill for a metallic finish.

### Chips & Tags
Small, high-contrast rectangular tags. For active states, use solid #FFFFFF with #000000 text. For inactive states, use #1A1A1A with white 1px borders.

### Progress Indicators
Linear bars only. The track is #1A1A1A, and the indicator is a brilliant white-to-silver gradient. No rounded caps; the bar ends must be perfectly vertical.

### Performance Data
Data visualizations should utilize thin, 1px white lines and solid white fills. No soft colors; use varying opacities of white and silver to differentiate data sets.