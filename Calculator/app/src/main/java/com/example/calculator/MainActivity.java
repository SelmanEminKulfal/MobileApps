package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private String currentInput = "";
    private double firstOperand = 0;
    private String operator = "";
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.result); // ID'yi XML'deki EditText ile eşle
        display.setText("0");

        setNumberButtonListeners();
        setOperatorButtonListeners();
        setSpecialFunctionListeners();
    }

    private void setNumberButtonListeners() {
        int[] numberButtonIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        };

        View.OnClickListener numberClickListener = view -> {
            Button button = (Button) view;
            if (isNewInput) {
                currentInput = "";
                isNewInput = false;
            }
            currentInput += button.getText().toString();
            display.setText(currentInput);
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }
    }

    private void setOperatorButtonListeners() {
        int[] operatorButtonIds = {
                R.id.btn_sum, R.id.btn_minus, R.id.btn_mult, R.id.btn_div, R.id.btn_power
        };

        View.OnClickListener operatorClickListener = view -> {
            Button button = (Button) view;
            if (!currentInput.isEmpty()) {
                firstOperand = Double.parseDouble(currentInput);
                operator = button.getText().toString();
                isNewInput = true;
            }
        };

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(operatorClickListener);
        }

        findViewById(R.id.btn_equal).setOnClickListener(view -> calculateResult());
        findViewById(R.id.btn_clean).setOnClickListener(view -> clearDisplay());
    }

    private void setSpecialFunctionListeners() {
        findViewById(R.id.btn_power).setOnClickListener(view -> {
            if (!currentInput.isEmpty()) {
                firstOperand = Double.parseDouble(currentInput);
                operator = "^";
                isNewInput = true;
            }
        });

        findViewById(R.id.btn_fact).setOnClickListener(view -> {
            if (!currentInput.isEmpty()) {
                int number = Integer.parseInt(currentInput);
                display.setText(String.valueOf(factorial(number)));
                isNewInput = true;
            }
        });

        findViewById(R.id.btn_sqrt).setOnClickListener(view -> {
            if (!currentInput.isEmpty()) {
                double number = Double.parseDouble(currentInput);
                if (number >= 0) {
                    display.setText(String.valueOf(Math.sqrt(number)));
                } else {
                    display.setText("Hata");
                }
                isNewInput = true;
            }
        });

        findViewById(R.id.btn_dot).setOnClickListener(view -> {
            if (!currentInput.contains(".")) {
                currentInput += ".";
                display.setText(currentInput);
            }
        });
    }

    private void calculateResult() {
        if (!currentInput.isEmpty() && !operator.isEmpty()) {
            double secondOperand = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "*":
                    result = firstOperand * secondOperand;
                    break;
                case "/":
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand;
                    } else {
                        display.setText("Hata");
                        return;
                    }
                    break;
                case "^":
                    result = Math.pow(firstOperand, secondOperand);
                    break;
            }
            display.setText(String.valueOf(result));
            currentInput = String.valueOf(result);
            operator = "";
            isNewInput = true;
        }
    }

    private int factorial(int n) {
        if (n < 0) return -1; // Negatif sayılar için faktöriyel tanımsız
        int fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

    private void clearDisplay() {
        currentInput = "";
        firstOperand = 0;
        operator = "";
        isNewInput = true;
        display.setText("0");
    }
}
