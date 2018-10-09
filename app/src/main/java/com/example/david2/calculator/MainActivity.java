package com.example.david2.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private final int MAX_DIGITS = 10;
    private final double ALMOST_ZERO = 0.0000000000001;

    private String currentOperand = "0";
    private int currentNumDigits = 1;
    private String previousOperand = "";

    private TextView inputOutputScreen;
    private TextView memoryScreen;

    private Double memory = 0d;

    private int[] numberButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};
    private Button btnMC, btnMAdd, btnMSub, btnMR;
    private Button btnClear, btnSign, btnDecimal, btnEqual;

    private String operator = null;
    private boolean lastIsOperator = false;
    private boolean lastIsEqual = false;
    private boolean lastIsMR = false;

    private DecimalFormat dfScientific = new DecimalFormat("0.#########E0");
    private DecimalFormat dfNormal = new DecimalFormat("0.#########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputOutputScreen = findViewById(R.id.inputOutputScreen);
        inputOutputScreen.setText("0");
        memoryScreen = findViewById(R.id.memoryScreen);
        memoryScreen.setText("M = 0");
        btnMC = findViewById(R.id.btnMC);
        btnMAdd = findViewById(R.id.btnMAdd);
        btnMSub = findViewById(R.id.btnMSub);
        btnMR = findViewById(R.id.btnMR);
        btnClear = findViewById(R.id.btnClear);
        btnSign = findViewById(R.id.btnSign);
        btnDecimal = findViewById(R.id.btnDecimal);
        btnEqual = findViewById(R.id.btnEqual);

        setNumberOnClickListener();
        setOperatorOnClickListener();
        setMemoryOnClickListener();
        setClearOnClickListener();
        setSignOnClickListener();
        setDecimalOnClickListener();
        setEqualOnClickListener();
    }

    //define and set OnClickListener for number buttons 0-9
    private void setNumberOnClickListener() {
        View.OnClickListener numberListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnNum = (Button) v;
                if(lastIsOperator || lastIsEqual || lastIsMR) {
                    currentOperand = btnNum.getText().toString();
                    currentNumDigits = 1;
                    lastIsOperator = false;
                    lastIsEqual = false;
                    lastIsMR = false;
                } else if(currentNumDigits == MAX_DIGITS) {
                    Toast.makeText(MainActivity.this, "Only 10 digits can be entered.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    switch (currentOperand) {
                        case "0":
                            currentOperand = btnNum.getText().toString();
                            currentNumDigits = 1;
                            break;
                        case "-0":
                            currentOperand = "-" + btnNum.getText().toString();
                            currentNumDigits = 1;
                            break;
                        default:
                            currentOperand += btnNum.getText().toString();
                            currentNumDigits++;
                            break;
                    }
                }
                inputOutputScreen.setText(currentOperand);
            }
        };
        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(numberListener);
        }
    }

    private void setOperatorOnClickListener() {
        View.OnClickListener operatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnOp = (Button) v;
                if(lastIsOperator) {
                    operator = btnOp.getText().toString();
                } else {
                    lastIsOperator = true;
                    if(operator == null) {
                        operator = btnOp.getText().toString();
                    } else {
                        String result = calculate();
                        currentOperand = result;
                        inputOutputScreen.setText(result);
                        operator = btnOp.getText().toString();
                    }
                    previousOperand = currentOperand;
                }
            }
        };
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(operatorListener);
        }
    }

    private void setMemoryOnClickListener() {
        btnMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory = 0d;
                memoryScreen.setText("M = 0");
            }
        });

        btnMAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(operator != null && !lastIsOperator) {
                    String result = calculate();
                    currentOperand = result;
                    inputOutputScreen.setText(result);
                }
                memory += Double.parseDouble(currentOperand);
                String memoryStr = "M = " + removeTrailingZeros(memory);
                memoryScreen.setText(memoryStr);
            }
        });

        btnMSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(operator != null && !lastIsOperator) {
                    String result = calculate();
                    currentOperand = result;
                    inputOutputScreen.setText(result);
                }
                memory -= Double.parseDouble(currentOperand);
                String memoryStr = "M = " + removeTrailingZeros(memory);
                memoryScreen.setText(memoryStr);
            }
        });

        btnMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastIsOperator = false;
                lastIsEqual = false;
                if(lastIsMR) {
                    return;
                }
                lastIsMR = true;
                currentOperand = memory.toString();
                inputOutputScreen.setText(currentOperand);
            }
        });
    }

    private void setClearOnClickListener() {
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOperand = "0";
                currentNumDigits = 1;
                previousOperand = "";
                inputOutputScreen.setText("0");
                memory = 0d;
                memoryScreen.setText("M = 0");
                operator = null;
                lastIsOperator = false;
                lastIsMR = false;
                lastIsEqual = false;
            }
        });
    }

    private void setSignOnClickListener() {
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastIsOperator) {
                    currentOperand = "-0";
                    currentNumDigits = 1;
                    lastIsOperator = false;
                } else if(currentOperand.charAt(0)=='-'){
                    currentOperand = currentOperand.substring(1,currentOperand.length());
                } else {
                    currentOperand = "-" + currentOperand;
                }
                inputOutputScreen.setText(currentOperand);
            }
        });
    }

    private void setDecimalOnClickListener() {
        btnDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastIsOperator = false;

                if(lastIsMR || lastIsEqual) {
                    lastIsMR = false;
                    lastIsEqual = false;
                    currentOperand = "0.";
                    currentNumDigits = 1;
                } else if(currentOperand.contains(".")) {
                    return;
                } else {
                    currentOperand += ".";
                }
                inputOutputScreen.setText(currentOperand);
            }
        });
    }

    private void setEqualOnClickListener() {
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastIsOperator = false;
                if(lastIsEqual) {
                    return;
                }
                lastIsEqual = true;
                if(operator != null) {
                    String result = calculate();
                    currentOperand = result;
                    inputOutputScreen.setText(result);
                    operator = null;
                }
            }
        });
    }

    private String calculate () {
        double result = 0;
        switch (operator) {
            case "+":
                result = Double.parseDouble(previousOperand) + Double.parseDouble(currentOperand);
                break;
            case "-":
                result = Double.parseDouble(previousOperand) - Double.parseDouble(currentOperand);
                break;
            case "*":
                result = Double.parseDouble(previousOperand) * Double.parseDouble(currentOperand);
                break;
            case "/":
                if(Double.parseDouble(currentOperand) == 0) {
                    Toast.makeText(MainActivity.this, "Cannot divide by zero, please clear.", Toast.LENGTH_SHORT).show();
                }
                result = Double.parseDouble(previousOperand) / Double.parseDouble(currentOperand);
                break;
        }
        return removeTrailingZeros(result);
    }

    private String removeTrailingZeros (Double d) {
        if(d > 1E7 || d < 1E-3) {
            return dfScientific.format(d);
        } else {
            return dfNormal.format(d);
        }
    }
}
