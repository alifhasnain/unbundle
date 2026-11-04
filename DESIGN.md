---
name: Unbundle
description: On-device .aab to universal APK, issued as a jet-age airline ticket.
colors:
  livery: "#1D2B5C"
  ticket-red: "#C8102E"
  livery-container: "#D5D9EC"
  carbon: "#4B3F8F"
  carbon-container: "#E4DDF5"
  carbon-deep: "#2E2566"
  ground: "#ECE9E2"
  coupon-stock: "#FBFAF7"
  ink: "#1B2140"
  ink-muted: "#555B70"
  outline: "#7E8399"
  rule: "#D3D3DA"
  paper-white: "#FFFFFF"
  night-livery: "#BAC4F2"
  night-on-livery: "#0E1A45"
  night-livery-container: "#2B3A73"
  night-on-livery-container: "#DDE1FF"
  night-red: "#FF6B78"
  night-carbon: "#CBBEFF"
  night-carbon-container: "#342B6E"
  night-carbon-deep: "#E6DEFF"
  night-ground: "#0B1028"
  night-stock: "#151C3D"
  night-ink: "#E6E7F2"
  night-ink-muted: "#AEB3CC"
  night-outline: "#747A99"
  night-rule: "#30385C"
typography:
  stamp:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "52sp"
    fontWeight: 700
    lineHeight: "44sp"
    letterSpacing: "8sp"
  airport-code:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "34sp"
    fontWeight: 700
    lineHeight: "36sp"
    letterSpacing: "3sp"
  headline:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "28sp"
    fontWeight: 700
    lineHeight: "36sp"
    letterSpacing: "0sp"
    fontFeature: "tnum"
  title:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "22sp"
    fontWeight: 700
    lineHeight: "28sp"
    letterSpacing: "1.5sp"
  figure:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "20sp"
    fontWeight: 600
    lineHeight: "24sp"
    letterSpacing: "0.15sp"
    fontFeature: "tnum"
  body:
    fontFamily: "Roboto, sans-serif"
    fontSize: "16sp"
    fontWeight: 400
    lineHeight: "24sp"
    letterSpacing: "0.5sp"
  body-small:
    fontFamily: "Roboto, sans-serif"
    fontSize: "12sp"
    fontWeight: 400
    lineHeight: "16sp"
    letterSpacing: "0.4sp"
  label-large:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "17sp"
    fontWeight: 600
    lineHeight: "20sp"
    letterSpacing: "1.2sp"
  label-medium:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "13sp"
    fontWeight: 600
    lineHeight: "16sp"
    letterSpacing: "1.2sp"
  label-small:
    fontFamily: "Barlow Condensed, Roboto Condensed, sans-serif"
    fontSize: "12sp"
    fontWeight: 600
    lineHeight: "16sp"
    letterSpacing: "1.4sp"
rounded:
  control: "6dp"
  stub-chamfer: "6dp"
  ticket-chamfer: "10dp"
  notch: "9dp"
spacing:
  hairline: "2dp"
  xs: "4dp"
  sm: "8dp"
  md: "12dp"
  lg: "16dp"
  max-width: "560dp"
components:
  button-primary:
    backgroundColor: "{colors.livery}"
    textColor: "{colors.paper-white}"
    typography: "{typography.label-large}"
    rounded: "{rounded.control}"
    height: "56dp"
  button-tonal:
    backgroundColor: "{colors.livery-container}"
    textColor: "{colors.livery}"
    typography: "{typography.label-large}"
    rounded: "{rounded.control}"
    height: "56dp"
  header-field:
    backgroundColor: "{colors.livery}"
    textColor: "{colors.paper-white}"
    typography: "{typography.title}"
    padding: "0 16dp 12dp 16dp"
  ticket:
    backgroundColor: "{colors.coupon-stock}"
    textColor: "{colors.ink}"
    rounded: "{rounded.ticket-chamfer}"
  ticket-strip:
    backgroundColor: "{colors.ticket-red}"
    textColor: "{colors.paper-white}"
    typography: "{typography.label-medium}"
    padding: "10dp 16dp"
  step-stub-pending:
    backgroundColor: "{colors.coupon-stock}"
    textColor: "{colors.ink-muted}"
    typography: "{typography.figure}"
    rounded: "{rounded.stub-chamfer}"
    padding: "8dp 4dp"
  step-stub-active:
    backgroundColor: "{colors.coupon-stock}"
    textColor: "{colors.carbon}"
    typography: "{typography.figure}"
    rounded: "{rounded.stub-chamfer}"
    padding: "8dp 4dp"
  step-stub-used:
    backgroundColor: "{colors.carbon-container}"
    textColor: "{colors.carbon-deep}"
    typography: "{typography.figure}"
    rounded: "{rounded.stub-chamfer}"
    padding: "8dp 4dp"
  field-box:
    backgroundColor: "{colors.coupon-stock}"
    textColor: "{colors.carbon}"
    typography: "{typography.figure}"
    padding: "8dp 0"
  barcode-stub:
    backgroundColor: "{colors.coupon-stock}"
    textColor: "{colors.ink}"
    height: "76dp"
    padding: "12dp 20dp"
  void-stamp:
    textColor: "{colors.ticket-red}"
    typography: "{typography.stamp}"
    rounded: "{rounded.control}"
    padding: "0 18dp"
---

# Design System: Unbundle

## Overview

**Creative North Star: "The Coupon Book"**

Unbundle is a jet-age airline ticket wallet. A conversion is a coupon book: each step is a stub used in order, and the universal APK is the ticket that remains. The carrier's navy livery owns the header, a red cheatline and ticket strip mark the issuing airline, and every filled figure lands in carbon purple, as if struck through a carbon sheet onto coupon stock. The ground is a warm paper grey; the ticket is a chamfered, side-notched slip with a perforated barcode stub.

The world is dense and operational, built for engineers who read exact figures. Condensed caps carry every label and figure; the system face carries only sentences. Motion is ticket-office mechanics, never decoration: dashes march while work is in progress, a plane advances one waypoint per finished step, stubs tear off once, figures strike once, and a failed conversion is stamped VOID. With the system remove-animations setting, everything cuts to its final state.

Material 3 supplies the structure (Scaffold, TopAppBar, Button, FilledTonalButton, dividers); the world arrives through color roles, type scale, and custom ticket shapes drawn on top of those controls.

**Key Characteristics:**
- Navy livery header with a 4dp red cheatline, kept identical in light and dark.
- Coupon-stock ticket on a paper ground, cut corners, side notches, perforated stub.
- Barlow Condensed caps for labels and figures; Roboto for sentences.
- Carbon ink for every filled value; muted ink for labels and empty dashes.
- One marching beat (700ms, 6/5dp dash) for every line that means "in progress".
- One-shot mechanical motion, saved across rotation, removed under reduce-motion.

## Colors

A carrier livery palette: navy and signal red over warm coupon paper, with a single carbon purple reserved for data.

### Primary
- **Carrier Livery** (livery): the header field, the filled Install button, the airport codes, the flown segment of the route, and the active stub's border. Also the tint of the ticket's shadow. In dark mode the header stays livery; on-surface roles lift to **Night Livery** (night-livery) for codes, the route, and the filled button.
- **Livery Wash** (livery-container): the tonal Save APK button. Its text is livery.

### Secondary
- **Ticket Red** (ticket-red): the cheatline under the header, the ticket strip, the plane and the waypoints it has passed, and every error state (VOID stub border and text, VOID stamp). The strip and cheatline keep this exact red in dark mode; the plane, waypoints, and error ink lift to **Night Red** (night-red).

### Tertiary
- **Carbon Ink** (carbon): every filled figure on a coupon or ticket: field values, route times, the active stub's elapsed time. Lifts to **Night Carbon** (night-carbon) in dark.
- **Carbon Wash** (carbon-container) with **Deep Carbon** (carbon-deep): the fill and figure of a used stub.

### Neutral
- **Paper Ground** (ground): the screen behind the ticket and the action area. Also the window background, so cold start shows paper, not white.
- **Coupon Stock** (coupon-stock): the ticket and pending/active stubs.
- **Ticket Ink** (ink): sentences, the app label, barcode bars.
- **Muted Ink** (ink-muted): field labels, endpoint labels (DEP, ARR IN), pending stubs, notes, the SHA line, the empty-value dash.
- **Route Grey** (outline): the unflown route, unpassed waypoints, pending stub borders, the perforation.
- **Hairline Rule** (rule): the dividers that box the fields.
- Night counterparts (night-ground, night-stock, night-ink, night-ink-muted, night-outline, night-rule): a navy-black ground under deep-navy stock.

### Named Rules
**The Carbon Rule.** Carbon purple means "a value the conversion produced". Labels, sentences, and empty placeholders never use it; filled figures always do.

**The Livery Constancy Rule.** The header field (livery), its cheatline, and the ticket strip (ticket-red) are carrier paint, not theme roles: they do not change between light and dark.

**The Red Means Issued-or-Void Rule.** Ticket red appears only as carrier paint (cheatline, strip), the plane's progress, and failure. It is never a decorative accent or a button fill.

## Typography

**Display Font:** Barlow Condensed (Medium 500, SemiBold 600, Bold 700; bundled, OFL)
**Body Font:** Roboto (system default)

**Character:** Barlow Condensed is the airline grotesk of departure boards and ticket stock: tall, narrow, tracked caps that fit long figures in narrow boxes. Roboto stays for anything read as a sentence, so notes and error reasons never become shouting caps.

### Hierarchy
- **Stamp** (700, 52sp, +8sp tracking): the VOID stamp only.
- **Airport Code** (700, 34sp, +3sp tracking): the AAB and APK endpoints, in livery.
- **Headline** (700, 28sp, tabular): big field values (version name, version code).
- **Title** (700, 22sp, +1.5sp tracking): the UNBUNDLE app bar title.
- **Figure** (600, 20sp, tabular): stub times, route times, ordinary field values, the package name.
- **Body** (Roboto 400, 16sp): the header file line, the app label, the error reason.
- **Body Small** (Roboto 400, 12sp): the per-state note above the buttons and the empty barcode stub hint.
- **Label Large** (600, 17sp, +1.2sp, uppercase): button text.
- **Label Medium** (600, 13sp, +1.2sp, uppercase): the ticket strip.
- **Label Small** (600, 12sp, +1.4sp, uppercase): field labels, stub labels, endpoint labels, the SHA-256 line (tabular).

### Named Rules
**The Tabular Figure Rule.** Every time, size, version, and hash renders with tabular numerals (`tnum`), so running clocks do not jitter and columns align.

**The Caps Are Labels Rule.** Uppercase condensed text is for labels, codes, statuses, and buttons. Anything that reads as a sentence is Roboto in sentence case.

## Layout

A single scrolling column, max 560dp wide and centered, inset 16dp, with 16dp between the stub row and the ticket. The action area is pinned to the bottom on the paper ground, with the same 560dp cap and insets (10dp top, 16dp bottom), a body-small note 10dp above the buttons, and two equal-weight 56dp buttons 12dp apart (one full-width button when choosing).

The stub row is four equal-weight stubs 8dp apart. Inside the ticket: the strip, the route (endpoint codes left and right, the route canvas between them, times aligned by baseline under each code), then hairline-ruled field boxes in a two-column grid where values pair (version name and code; SDK and size) with 12dp inside the vertical rule. The 76dp barcode stub closes the ticket. Field boxes pad 8dp vertically with 2dp between label and value. Spacing steps are 2, 4, 8, 12, 16dp.

## Elevation & Depth

Paper on paper. The only shadow is the ticket's: a low 3dp elevation tinted with carrier livery for both ambient and spot light, so it reads as a slip lifted off the ground rather than a floating card. Stubs, fields, and buttons are flat; hierarchy comes from fill (coupon stock vs. carbon wash), border weight, and ruled hairlines.

### Shadow Vocabulary
- **Ticket Lift** (`shadow(3dp, ticketShape, ambientColor = livery, spotColor = livery)`): the ticket only.

### Named Rules
**The One Slip Rule.** Only the issued ticket casts a shadow. New paper elements sit flat on the ground unless they are the document the user takes away.

## Shapes

Two corner languages, by material. **Paper is cut**: the ticket chamfers its corners (10dp), carries a semicircular notch (9dp radius) on each side at the top of the barcode stub, and is ruled across that line by a 4/4dp perforation inset 14dp from each edge. Step stubs chamfer at 6dp. **Controls and ink are rounded**: buttons and the VOID stamp's frame use a gentle 6dp radius. The ticket wears a 1dp livery border at 55% alpha; stubs wear 1dp borders (2dp when active), dashed while pending or active, solid once used or void.

## Components

### Buttons
Tactile ticket-counter buttons, never pills.
- **Shape:** gently rounded (6dp), 56dp tall, equal width in a pair.
- **Primary (filled):** livery fill, white label-large caps. Install, Choose Bundle, Choose Another Bundle. Sits on the right of the pair.
- **Tonal:** livery-wash fill, livery text. Save APK, on the left.
- **Disabled:** Material 3 defaults while no APK exists.

### Header Field
- Livery TopAppBar titled UNBUNDLE (title style, white) with a plus action to choose another bundle once one is loaded and idle.
- Under it, a body line in white at 86% alpha: the bundle name and size, or the idle invitation. Max two lines.
- Closed by a 4dp ticket-red cheatline.

### Step Stubs (Signature)
Four coupons, COPY / READ / CONVERT / PACKAGE, each a 6dp-chamfered box with a label-small caps label over a tabular figure.
- **Pending:** coupon stock, 1dp dashed route-grey border, muted dash.
- **Active:** coupon stock, 2dp dashed livery border marching on the shared beat, carbon running time.
- **Used:** carbon wash, solid 1dp carbon border, deep-carbon final time. Tears off once: dips 6dp and tilts -4 degrees, peaking at 110ms, settling by 420ms.
- **Void:** red at 10% fill, solid red border, red VOID.

### Ticket (Signature)
- Coupon-stock slip in the cut-and-notched shape with the Ticket Lift shadow.
- **Strip:** ticket red, white label-medium caps: UNIVERSAL APK left, live status right (NOT ISSUED, PRINTING, VALID, VOID).
- **Route:** AAB and APK codes in livery; under each, a muted label-small tag and a carbon tabular time: DEP with the clock time, then ARR / EN ROUTE / ARR IN / STOPPED with the elapsed duration. Between them, a 2dp line: solid livery where flown, dashed route grey ahead, three 3.5dp waypoints that turn red as the plane passes. A 26dp Material Symbols plane in red advances one waypoint per finished step over 700ms and bobs 2dp on the beat while working.
- **Field boxes:** label-small muted label, then a carbon value; the package row merges APP and PACKAGE, showing the app icon (28dp), the app label in body ink, and the package name in carbon; tapping copies it. A void ticket shows only a REASON box in body ink.
- **Barcode stub:** 76dp below the perforation; bars drawn from the APK's SHA-256 bits in ink, then a tabular SHA line; tapping copies the hash. Before conversion a body-small hint; when void, blank for the stamp.

### VOID Stamp
Red stamp-style caps in a 4dp red frame (6dp radius), rotated -8 degrees at 90% opacity, landing on the barcode stub: scales from 1.6 to 1 over 260ms, once.

### Motion
- **The Shared Beat:** every marching line (active stub border, unflown route) uses a 6/5dp dash advancing one period per 700ms, linear.
- **Carbon Strike:** a value fades in from 3dp right and 2dp up and settles into register over 320ms, staggered 70ms per field index.
- One-shot effects (strike, tear-off, stamp) are saved with rememberSaveable and never replay on rotation or theme change. When animator duration scale is 0, all motion is skipped and final states show.

## Do's and Don'ts

### Do:
- **Do** put every produced value in carbon (carbon / night-carbon) and every label in muted ink label-small caps.
- **Do** use tabular figures for every time, size, version, and hash.
- **Do** keep the livery header, 4dp ticket-red cheatline, and ticket-red strip identical in light and dark.
- **Do** cut paper corners (10dp ticket, 6dp stub) and round control corners (6dp).
- **Do** drive every in-progress line from the one 700ms beat and 6/5dp dash.
- **Do** play mechanical effects once, save that they played, and skip them under remove-animations.
- **Do** keep sentences (notes, reasons, file line) in Roboto sentence case.

### Don't:
- **Don't** use carbon purple for labels, links, or chrome; it means produced data.
- **Don't** use ticket red as a button fill or decorative accent; it is carrier paint, progress, and failure.
- **Don't** give a second element a shadow; only the ticket lifts.
- **Don't** add another dash rhythm or animation tempo for progress; the perforation's static 4/4dp dash is a cut line, not progress.
- **Don't** fake a percentage: progress advances one waypoint per finished step.
