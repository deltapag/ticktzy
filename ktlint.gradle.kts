/**
 * KtLint Configuration
 *
 * This file configures ktlint, a Kotlin linter and formatter
 * that enforces code style consistency.
 */

ktlint {
    version.set("1.1.1")
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    
    // Use baseline to ignore existing violations
    baseline.set(file("app/config/ktlint/baseline.xml"))
    
    // Relax some rules to avoid breaking existing code
    filter {
        exclude("**/build/**")
        exclude("**/gradle/**")
        exclude("**/*.gradle.kts")
    }
}
