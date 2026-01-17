# Navigation 3 Scene Animations Sample

This is a sample project demonstrating **two issues** with Jetpack Compose Navigation 3 and `ListDetailSceneStrategy` in adaptive layouts.

## 🐛 Issues Demonstrated

### Issue 1: Transition animations don't work between top-level destinations in two-pane mode

**Expected behavior:**
- Single-pane mode (< 600dp): Slide animations work correctly ✅
- Two-pane mode (≥ 600dp): Animations should work when changing top-level destinations (Home → Downloads)

**Actual behavior:**
- Two-pane mode: Screen "jumps" without any animation when switching between top-level destinations that have `listPane()` metadata ❌

**Detailed description:** [Issue 1 - Transition animations](app/src/main/java/com/example/nav3sceneanimations/ui/navigation/Jetpack%20Compose%20Navigation%203%20-%20Transition%20animations%20don't%20work%20with%20ListDetailSceneStrategy%20in%20adaptive%20layout.md)

---

### Issue 2: No animations when changing content within individual panes

**Expected behavior:**
- When selecting a different movie in list pane (P1), detail pane (P2) should animate the content change
- When clicking "Similar Movie" in detail pane (P2), the new content should animate in
- When clicking a movie from filmography in extra pane (P3), detail pane (P2) should animate

**Actual behavior:**
- Content in panes changes instantly without any animation
- Animations only work when transitioning between single-pane and multi-pane modes

**Detailed description:** [Issue 2 - Pane content animations](app/src/main/java/com/example/nav3sceneanimations/ui/navigation/How%20to%20add%20animations%20when%20changing%20content%20in%20individual%20panes%20of%20ListDetailSceneStrategy%20in%20Jetpack%20Compose%20Navigation%203.md)

---

## 📱 App Structure

### Screen Types (Metadata)

| Pane | Metadata | Screens |
|------|----------|---------|
| P1 (List) | `ListDetailSceneStrategy.listPane()` | Home, Search, MyList, Downloads |
| P2 (Detail) | `ListDetailSceneStrategy.detailPane()` | MovieDetail, TvShowDetail |
| P3 (Extra) | `ListDetailSceneStrategy.extraPane()` | PersonDetail |
| - | none | Profile |

### Visual Layout

```
Two-pane mode (≥ 600dp):
┌─────────────────────┬─────────────────────────────────────────────┐
│                     │                                             │
│       List          │           Movie Detail                      │
│       (P1)          │              (P2)                           │
│                     │                                             │
│   Home/Search/...   │        MovieDetail/TvShowDetail             │
│                     │                                             │
└─────────────────────┴─────────────────────────────────────────────┘

Three-pane mode (≥ 840dp with extra pane):
┌─────────────────────┬─────────────────────────┬─────────────────────┐
│                     │                         │                     │
│       List          │      Movie Detail       │   Person Detail     │
│       (P1)          │         (P2)            │       (P3)          │
│                     │                         │                     │
└─────────────────────┴─────────────────────────┴─────────────────────┘

Single-pane mode (< 600dp):
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                      Current Screen                                 │
│                                                                     │
│            Home → MovieDetail → PersonDetail → ...                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
app/src/main/java/com/example/nav3sceneanimations/
├── MainActivity.kt                 # Main activity with edge-to-edge
├── AppContent.kt                   # Root composable
│
├── ui/
│   ├── navigation/
│   │   ├── NavigationRoot.kt       # Main navigation setup with NavDisplay
│   │   ├── NavigationState.kt      # Navigation state management with multiple back stacks
│   │   ├── Navigator.kt            # Navigation logic (hierarchical, cross-reference)
│   │   ├── Route.kt                # Sealed interface with all navigation routes
│   │   ├── BottomNavItem.kt        # Top-level destination definitions
│   │   ├── NavTransitions.kt       # Transition animation definitions
│   │   ├── AdaptiveNavigationScaffold.kt  # Adaptive scaffold (BottomBar/Rail)
│   │   │
│   │   └── [Issue descriptions as .md files]
│   │
│   └── screens/
│       ├── home/
│       │   └── HomeScreen.kt       # Home screen (P1 - listPane)
│       ├── search/
│       │   └── SearchScreen.kt     # Search screen (P1 - listPane)
│       ├── mylist/
│       │   └── MyListScreen.kt     # My List screen (P1 - listPane)
│       ├── downloads/
│       │   └── DownloadsScreen.kt  # Downloads screen (P1 - listPane)
│       ├── profile/
│       │   └── ProfileScreen.kt    # Profile screen (standalone - no metadata)
│       └── detail/
│           ├── MovieDetailScreen.kt    # Movie detail (P2 - detailPane)
│           ├── TvShowDetailScreen.kt   # TV show detail (P2 - detailPane)
│           └── PersonDetailScreen.kt   # Person detail (P3 - extraPane)
```

---

## 🔧 Key Components

### NavigationRoot.kt
Main navigation setup using:
- `NavDisplay` with `ListDetailSceneStrategy`
- Global transition specs for slide animations
- Entry provider with metadata assignments

### Navigator.kt
Handles complex navigation logic:
- **Hierarchical navigation**: P1 → P2 → P3
- **Cross-reference navigation**: Update P2 from P3 (in three-pane mode)
- **Linear navigation**: Stack-based in single-pane mode
- **Back navigation**: Proper stack unwinding

### NavigationState.kt
Manages navigation state:
- Multiple back stacks (one per top-level destination)
- Serializable state for process death survival
- Decorated entries with SaveableStateHolder and ViewModelStore

### AdaptiveNavigationScaffold.kt
Adaptive navigation UI:
- **Compact (< 600dp)**: ShortNavigationBar with vertical items
- **Medium (600-840dp)**: ShortNavigationBar with horizontal items
- **Expanded (> 840dp)**: NavigationRail

---

## 🛠️ Tech Stack

| Component | Version |
|-----------|---------|
| Kotlin | 2.2.21 |
| Compose BOM | 2026.01.00 |
| Navigation 3 | 1.1.0-alpha02 |
| Material 3 Adaptive | 1.3.0-alpha06 |
| Material 3 | 1.5.0-alpha12 |
| Android Gradle Plugin | 8.13.2 |
| Min SDK | 24 |
| Target SDK | 36 |

---

## 🚀 How to Run

1. Clone the repository
2. Open in Android Studio (Ladybug or newer recommended)
3. Sync Gradle
4. Run on device/emulator

### Testing the Issues

**Issue 1 - No animations between top-level destinations:**
1. Use a tablet or resize emulator to ≥ 600dp width
2. Navigate to any screen that opens detail (e.g., click "Open movie detail" on Home)
3. Now try switching between Home, Search, My List, Downloads tabs
4. Notice: No animation when switching tabs (screen just jumps)

**Issue 2 - No animations in pane content:**
1. Use a tablet or resize emulator to ≥ 600dp width
2. Open a movie detail from Home
3. Click "Similar movie 1" or "Similar movie 2" in detail pane
4. Notice: Content changes instantly without animation

---

## 📝 Notes

- The `Profile` screen intentionally has no `listPane()` metadata to demonstrate that animations DO work for screens without this metadata
- Each detail route has a unique `instanceId` (UUID) to ensure Navigation 3 treats each navigation as unique
- The project uses multiple back stacks pattern for proper tab state preservation

---

## 🔗 Related Links

- [Navigation 3 Documentation](https://developer.android.com/guide/navigation/navigation-3)
- [Navigation 3 Recipes](https://github.com/android/nav3-recipes)
- [Material 3 Adaptive Layouts](https://developer.android.com/develop/ui/compose/layouts/adaptive)
- [Window Size Classes](https://m3.material.io/foundations/layout/applying-layout/window-size-classes)
