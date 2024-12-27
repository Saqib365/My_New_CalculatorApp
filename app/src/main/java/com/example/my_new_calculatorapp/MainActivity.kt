package com.example.my_new_calculatorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.my_new_calculatorapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var input = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(binding.root)

        // Clear button functionality
        binding.Clear.setOnClickListener {
            input = ""
            binding.textInput.text = ""
            binding.textResult.text = ""
        }

        // Backspace button functionality
        binding.Backspace.setOnClickListener {
            if (input.isNotEmpty()) {
                input = input.dropLast(1)
                binding.textInput.text = input
            }
        }

        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btndot
        )
        for (button in numberButtons) {
            button.setOnClickListener {
                input += button.text.toString() // Ensure text is converted to String
                binding.textInput.text = input // Dynamically update the input field
            }
        }

        // Operator buttons functionality
        binding.Plus.setOnClickListener { handleOperations("+") }
        binding.Minus.setOnClickListener { handleOperations("-") }
        binding.multiply.setOnClickListener { handleOperations("*") }
        binding.division.setOnClickListener { handleOperations("/") }

        // Equal button functionality
        binding.equal.setOnClickListener {
            val resultValue = evaluateExpression()
            if (!resultValue.isNaN()) {
                binding.textResult.text = String.format(Locale.getDefault(), "%.2f", resultValue)
            } else {
                binding.textResult.text = "Error"
            }
        }
    }

    // Handles operator inputs
    private fun handleOperations(op: String) {
        if (binding.textInput.text.isNotEmpty() && !input.endsWith(op)) {
            input += op
            binding.textInput.text = input
        }
    }

    // Evaluates the input expression manually
    private fun evaluateExpression(): Double {
        return try {
            // Split the input into tokens (numbers and operators)
            val sanitizedInput = input.replace("X", "*")
            val tokens = sanitizedInput.split("(?<=[-+*/])|(?=[-+*/])".toRegex())
            calculateExpression(tokens)
        } catch (e: Exception) {
            Double.NaN
        }
    }

    // Calculates the result from a list of tokens
    private fun calculateExpression(tokens: List<String>): Double {
        val stack = mutableListOf<Double>()
        var operator: String? = null

        for (token in tokens) {
            when {
                token.matches(Regex("[-+*/]")) -> operator = token // Save operator
                else -> {
                    val number = token.toDoubleOrNull() ?: continue
                    if (operator == null) {
                        stack.add(number)
                    } else {
                        val firstOperand = stack.removeAt(stack.size - 1)
                        val result = when (operator) {
                            "+" -> firstOperand + number
                            "-" -> firstOperand - number
                            "*" -> firstOperand * number
                            "/" -> if (number != 0.0) firstOperand / number else Double.NaN
                            else -> firstOperand
                        }
                        stack.add(result)
                        operator = null
                    }
                }
            }
        }
        return stack.firstOrNull() ?: Double.NaN
    }
}
