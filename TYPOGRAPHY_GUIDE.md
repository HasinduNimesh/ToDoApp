# Modern Typography Guide for ModernTodo

## Overview
The ModernTodo app now uses a modern typography system with improved fonts and spacing for better readability and visual appeal.

## Font Family
- **Primary Font**: `FontFamily.SansSerif` - A clean, modern system font that works across all Android devices
- **Fallback Options**: Google Fonts (Inter, Poppins) are configured but will fallback to system fonts for reliability

## Typography Styles

### Standard Material 3 Styles
- `displayLarge` - Hero text (57sp)
- `displayMedium` - Large hero text (45sp) 
- `displaySmall` - Small hero text (36sp)
- `headlineLarge` - Main headings (32sp)
- `headlineMedium` - Section headings (28sp)
- `headlineSmall` - Subsection headings (24sp)
- `titleLarge` - Card titles (22sp)
- `titleMedium` - UI element titles (16sp)
- `titleSmall` - Small titles (14sp)
- `bodyLarge` - Main content (16sp)
- `bodyMedium` - Secondary content (14sp)
- `bodySmall` - Caption text (12sp)
- `labelLarge` - Button text (14sp)
- `labelMedium` - Small UI labels (12sp)
- `labelSmall` - Tiny labels (11sp)

### Custom Modern Styles
- `ModernTextStyles.cardTitle` - Perfect for card headers
- `ModernTextStyles.cardSubtitle` - Ideal for card descriptions
- `ModernTextStyles.buttonText` - Enhanced button typography
- `ModernTextStyles.captionText` - For image captions and notes
- `ModernTextStyles.appBarTitle` - Optimized for app bar titles

## Usage Examples

### In Composable Functions
```kotlin
Text(
    text = "Main Title",
    style = MaterialTheme.typography.headlineSmall
)

Text(
    text = "Card Title",
    style = ModernTextStyles.cardTitle
)

Text(
    text = "Description",
    style = ModernTextStyles.cardSubtitle
)
```

### Modern Improvements
1. **Better Letter Spacing**: Optimized for digital reading
2. **Improved Line Heights**: Better vertical rhythm
3. **Consistent Font Weights**: Proper hierarchy
4. **Accessibility**: Enhanced readability across all devices

## Implementation Notes
- Uses system fonts for maximum compatibility
- Google Fonts (Inter, Poppins) available as alternatives
- Automatic fallback to system fonts if custom fonts fail
- All styles follow Material Design 3 guidelines
- Optimized for both light and dark themes

## Files Modified
- `ui/theme/Type.kt` - Main typography configuration
- `ui/screens/settings/NotificationSettingsScreen.kt` - Example implementation
- `res/font/` - Font resource files
- `res/values/font_certs.xml` - Font provider configuration

## Future Enhancements
You can easily switch to Google Fonts by uncommenting the custom font configurations in `Type.kt` and ensuring the font files are properly loaded.
