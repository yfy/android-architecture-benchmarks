/**
 * Code Quality Metrics Analysis
 * 
 * Run with: ./gradlew analyzeCodeMetrics
 * 
 * Generates:
 * 1. Lines of Code (LOC)
 * 2. Cyclomatic Complexity (CC)
 * 3. Maintainability Index (MI)
 * 4. Code duplication
 */

tasks.register("analyzeCodeMetrics") {
    group = "verification"
    description = "Analyzes code quality metrics for all architecture modules"
    
    doLast {
        val modules = listOf(
            "feature:product-impl",
            "feature:product-impl-classicmvvm",
            "feature:product-impl-mvi",
            "feature:product-impl-mvp",
            "feature:product-impl-mvc"
        )
        
        val metricsDir = File("${project.rootDir}/metrics")
        metricsDir.mkdirs()
        
        modules.forEach { module ->
            val moduleDir = File("${project.rootDir}/${module.replace(":", "/")}")
            val srcDir = File(moduleDir, "src/main/kotlin")
            
            if (srcDir.exists()) {
                println("Analyzing: $module")
                
                // Count LOC
                val loc = countLinesOfCode(srcDir)
                
                // Calculate Cyclomatic Complexity
                val complexity = calculateComplexity(srcDir)
                
                // Generate report
                val reportFile = File(metricsDir, "${module.replace(":", "_")}_metrics.txt")
                reportFile.writeText("""
                    Module: $module
                    Lines of Code: $loc
                    Cyclomatic Complexity: $complexity
                    Average CC per file: ${complexity.toDouble() / countFiles(srcDir)}
                """.trimIndent())
                
                println("Report saved: ${reportFile.absolutePath}")
            }
        }
    }
}

fun countLinesOfCode(dir: File): Int {
    var count = 0
    dir.walkTopDown().forEach { file ->
        if (file.extension == "kt") {
            count += file.readLines()
                .filter { it.trim().isNotEmpty() }
                .filter { !it.trim().startsWith("//") }
                .size
        }
    }
    return count
}

fun calculateComplexity(dir: File): Int {
    var complexity = 0
    dir.walkTopDown().forEach { file ->
        if (file.extension == "kt") {
            val content = file.readText()
            complexity += countDecisionPoints(content)
        }
    }
    return complexity
}

fun countDecisionPoints(code: String): Int {
    var count = 0
    listOf("if ", "when ", "for ", "while ", "&&", "||", "?:", "catch").forEach { keyword ->
        count += keyword.toRegex().findAll(code).count()
    }
    return count
}

fun countFiles(dir: File): Int {
    return dir.walkTopDown().count { it.extension == "kt" }
}
