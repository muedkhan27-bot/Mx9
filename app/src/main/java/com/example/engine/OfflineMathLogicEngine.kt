package com.example.engine

import java.util.Locale
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object OfflineMathLogicEngine {

    fun tryEvaluateMath(query: String): String? {
        val q = query.lowercase(Locale.ROOT).trim()

        // 1. Percentage: "15% of 200" or "what is 20 percent of 500"
        val percentRegex = Regex("""(?:what is\s+)?(\d+(?:\.\d+)?)\s*(?:%|percent)\s+of\s+(\d+(?:\.\d+)?)""")
        percentRegex.find(q)?.let { match ->
            val (pctStr, valStr) = match.destructured
            val pct = pctStr.toDoubleOrNull() ?: return null
            val value = valStr.toDoubleOrNull() ?: return null
            val result = (pct / 100.0) * value
            return "$pct% of $value is ${formatNumber(result)}, sir."
        }

        // 2. Square root: "square root of 144" or "sqrt 81"
        val sqrtRegex = Regex("""(?:square root of|sqrt)\s+(\d+(?:\.\d+)?)""")
        sqrtRegex.find(q)?.let { match ->
            val (numStr) = match.destructured
            val num = numStr.toDoubleOrNull() ?: return null
            if (num < 0) return "The square root of negative numbers yields an imaginary vector, sir."
            val result = sqrt(num)
            return "The square root of $num is ${formatNumber(result)}, sir."
        }

        // 3. Power: "2 power 8" or "3 to the power of 4" or "2^8"
        val powerRegex = Regex("""(\d+(?:\.\d+)?)\s*(?:\^|power|to the power of)\s*(\d+(?:\.\d+)?)""")
        powerRegex.find(q)?.let { match ->
            val (baseStr, expStr) = match.destructured
            val base = baseStr.toDoubleOrNull() ?: return null
            val exp = expStr.toDoubleOrNull() ?: return null
            val result = base.pow(exp)
            return "$base raised to the power of $exp equals ${formatNumber(result)}, sir."
        }

        // 4. Basic arithmetic: "123 + 456", "what is 54 * 23", "calculate 1000 / 25", "500 - 120"
        val cleaned = q.replace("what is", "")
            .replace("calculate", "")
            .replace("solve", "")
            .replace("compute", "")
            .replace("times", "*")
            .replace("multiplied by", "*")
            .replace("x", "*")
            .replace("divided by", "/")
            .replace("over", "/")
            .replace("plus", "+")
            .replace("minus", "-")
            .trim()

        val simpleArithRegex = Regex("""^(\d+(?:\.\d+)?)\s*([\+\-\*/])\s*(\d+(?:\.\d+)?)$""")
        simpleArithRegex.find(cleaned)?.let { match ->
            val (n1Str, op, n2Str) = match.destructured
            val n1 = n1Str.toDoubleOrNull() ?: return null
            val n2 = n2Str.toDoubleOrNull() ?: return null
            val result = when (op) {
                "+" -> n1 + n2
                "-" -> n1 - n2
                "*" -> n1 * n2
                "/" -> {
                    if (n2 == 0.0) return "Division by zero leads to undefined quantum singularities, sir."
                    n1 / n2
                }
                else -> return null
            }
            return "$n1 $op $n2 equals ${formatNumber(result)}, sir."
        }

        // 5. Trigonometry: "sin 90", "cos 0", "tan 45"
        val trigRegex = Regex("""(sin|cos|tan)\s+(\d+(?:\.\d+)?)""")
        trigRegex.find(q)?.let { match ->
            val (func, degStr) = match.destructured
            val deg = degStr.toDoubleOrNull() ?: return null
            val rad = Math.toRadians(deg)
            val result = when (func) {
                "sin" -> sin(rad)
                "cos" -> cos(rad)
                "tan" -> tan(rad)
                else -> 0.0
            }
            return "$func($deg°) equals ${formatNumber(result)}, sir."
        }

        return null
    }

    fun tryEvaluateConversion(query: String): String? {
        val q = query.lowercase(Locale.ROOT).trim()

        // Temperature: Celsius <-> Fahrenheit
        val cToFRegex = Regex("""(?:convert\s+)?(-?\d+(?:\.\d+)?)\s*(?:celsius|°c|c)\s*(?:to|in)\s*(?:fahrenheit|°f|f)""")
        cToFRegex.find(q)?.let { match ->
            val (valStr) = match.destructured
            val c = valStr.toDoubleOrNull() ?: return null
            val f = (c * 9.0 / 5.0) + 32.0
            return "$c°C corresponds to ${formatNumber(f)}°F, sir."
        }

        val fToCRegex = Regex("""(?:convert\s+)?(-?\d+(?:\.\d+)?)\s*(?:fahrenheit|°f|f)\s*(?:to|in)\s*(?:celsius|°c|c)""")
        fToCRegex.find(q)?.let { match ->
            val (valStr) = match.destructured
            val f = valStr.toDoubleOrNull() ?: return null
            val c = (f - 32.0) * 5.0 / 9.0
            return "$f°F corresponds to ${formatNumber(c)}°C, sir."
        }

        // Distance: Miles <-> Km
        val miToKmRegex = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*(?:miles|mile|mi)\s*(?:to|in)\s*(?:km|kilometers|kilometer)""")
        miToKmRegex.find(q)?.let { match ->
            val (valStr) = match.destructured
            val mi = valStr.toDoubleOrNull() ?: return null
            val km = mi * 1.60934
            return "$mi miles equals ${formatNumber(km)} kilometers, sir."
        }

        val kmToMiRegex = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*(?:km|kilometers|kilometer)\s*(?:to|in)\s*(?:miles|mile|mi)""")
        kmToMiRegex.find(q)?.let { match ->
            val (valStr) = match.destructured
            val km = valStr.toDoubleOrNull() ?: return null
            val mi = km / 1.60934
            return "$km kilometers equals ${formatNumber(mi)} miles, sir."
        }

        // Weight: Kg <-> Lbs
        val kgToLbsRegex = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*(?:kg|kilograms|kilos)\s*(?:to|in)\s*(?:lbs|pounds|pound)""")
        kgToLbsRegex.find(q)?.let { match ->
            val (valStr) = match.destructured
            val kg = valStr.toDoubleOrNull() ?: return null
            val lbs = kg * 2.20462
            return "$kg kilograms equals ${formatNumber(lbs)} pounds, sir."
        }

        val lbsToKgRegex = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*(?:lbs|pounds|pound)\s*(?:to|in)\s*(?:kg|kilograms|kilos)""")
        lbsToKgRegex.find(q)?.let { match ->
            val (valStr) = match.destructured
            val lbs = valStr.toDoubleOrNull() ?: return null
            val kg = lbs / 2.20462
            return "$lbs pounds equals ${formatNumber(kg)} kilograms, sir."
        }

        // Digital Storage: GB <-> MB
        val gbToMbRegex = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*(?:gb|gigabytes)\s*(?:to|in)\s*(?:mb|megabytes)""")
        gbToMbRegex.find(q)?.let { match ->
            val (valStr) = match.destructured
            val gb = valStr.toDoubleOrNull() ?: return null
            val mb = gb * 1024.0
            return "$gb GB is equivalent to ${formatNumber(mb)} MB in binary computation, sir."
        }

        return null
    }

    private fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", value)
        }
    }
}
