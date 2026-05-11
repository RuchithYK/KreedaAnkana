package com.example.kreedaankana.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════
//  NEON SPORTS — Primary Palette
// ══════════════════════════════════════════

// Backgrounds
val SportBlack       = Color(0xFF080810)   // deepest bg
val SportSurface     = Color(0xFF111120)   // card surface
val SportSurface2    = Color(0xFF1A1A2E)   // elevated card
val SportBorder      = Color(0xFF2A2A4A)   // subtle border
val SportBorderLight = Color(0xFF3A3A5A)   // light border

// Text
val SportWhite       = Color(0xFFFFFFFF)
val SportGrey        = Color(0xFF7070A0)
val SportGreyLight   = Color(0xFF9090B8)
val SportGreyMuted   = Color(0xFF404060)

// Primary accent — electric orange
val SportOrange      = Color(0xFFFF6B00)
val SportOrangeLight = Color(0xFFFF8C40)
val SportOrangeDark  = Color(0xFFCC4A00)

// Secondary accent — neon green (live/open)
val SportGreen       = Color(0xFF00E676)
val SportGreenDark   = Color(0xFF00B050)

// Tertiary accent — electric blue (accepted/info)
val SportBlue        = Color(0xFF2979FF)
val SportBlueDark    = Color(0xFF1565C0)

// Status / danger
val SportRed         = Color(0xFFFF1744)
val SportRedBg       = Color(0xFF2A0010)
val SportRedBorder   = Color(0xFFAA0030)

// Gold — winner / highlight
val SportGold        = Color(0xFFFFD600)
val SportGoldDark    = Color(0xFFFF8F00)

// Gradient helpers (used as gradient stop colours)
val SportGradStart   = Color(0xFFFF6B00)   // orange
val SportGradMid     = Color(0xFFFF0080)   // hot pink
val SportGradEnd     = Color(0xFF7C00FF)   // electric purple

// ══════════════════════════════════════════
//  BACKWARD-COMPAT ALIASES (old Stitch names → Sport names)
// ══════════════════════════════════════════
val StitchBlack       = SportBlack
val StitchSurface     = SportSurface
val StitchSurface2    = SportSurface2
val StitchBorder      = SportBorder
val StitchBorderLight = SportBorderLight
val StitchWhite       = SportWhite
val StitchGrey        = SportGrey
val StitchGreyLight   = SportGreyLight
val StitchGreyMuted   = SportGreyMuted
val StitchAccentGold  = SportGold
val StitchAccentOrange= SportOrange
val StitchStatusOpen  = SportGreen
val StitchStatusAccepted = SportBlue
val StitchDangerBg    = SportRedBg
val StitchDangerText  = SportRed
val StitchDangerBorder= SportRedBorder

// Legacy Obsidian compat
val ObsidianBlack           = SportBlack
val ObsidianSurface         = SportSurface2
val ObsidianSurfaceVariant  = SportBorder
val ObsidianText            = SportWhite
val ObsidianTextSecondary   = SportGreyLight
val LightBackground         = Color(0xFFF5F5F5)
val LightSurface            = Color(0xFFFFFFFF)
val LightSurfaceVariant     = Color(0xFFE0E0E0)
val LightText               = Color(0xFF121212)
val LightTextSecondary      = Color(0xFF555555)
val AccentGold              = SportGold
val ErrorRed                = SportRed
val BrandGreen              = Color(0xFF1B5E20)
val BrandGreenLight         = Color(0xFF4CAF50)