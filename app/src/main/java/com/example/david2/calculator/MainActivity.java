package com.example.david2.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private String currentInput = "0";
    private int currentNumDigits = 1;
    private String previousInputs = "";
    private String inputToDisplay = "";

    private TextView inputScreen;
    private TextView outputScreen;
    private TextView memoryScreen;

    private Double memory = 0d;

    private int[] numberButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};
    private Button btnMC, btnMAdd, btnMSub, btnMR;
    private Button btnClear, btnSign, btnDecimal, btnEqual;

    private boolean hasOperator = false;
    private boolean lastIsOperator = false;
    private boolean lastIsMR = false;
    private boolean lastIsEqual = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputScreen = findViewById(R.id.inputScreen);
        inputScreen.setText("0");
        outputScreen = findViewById(R.id.outputScreen);
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
                lastIsOperator = false;
                if(currentNumDigits == 10) {
                    Toast.makeText(MainActivity.this, "Only 10 digits can be entered.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(lastIsMR) {
                    lastIsMR = false;
                    currentInput = btnNum.getText().toString();
                    currentNumDigits = 1;
                } else {
                    switch (currentInput) {
                        case "0":
                            currentInput = btnNum.getText().toString();
                            currentNumDigits = 1;
                            break;
                        case "-0":
                            currentInput = "-" + btnNum.getText().toString();
                            currentNumDigits = 1;
                            break;
                        default:
                            currentInput += btnNum.getText().toString();
                            currentNumDigits++;
                            break;
                    }
                }
                inputToDisplay = previousInputs + currentInput;
                inputScreen.setText(inputToDisplay);
                if(hasOperator) {
                    outputScreen.setText(calculate());
                }
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
                lastIsMR = false;
                if(lastIsOperator) {
                    previousInputs = previousInputs.substring(0, previousInputs.length() - 1)
                            + btnOp.getText().toString();
                } else {
                    previousInputs += currentInput;
                    previousInputs += btnOp.getText().toString();
                    currentInput = "0";
                    currentNumDigits = 1;
                    inputToDisplay = previousInputs + currentInput;
                    inputScreen.setText(inputToDisplay);
                    outputScreen.setText("");
                    hasOperator = true;
                    lastIsOperator = true;
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
                memory += Double.parseDouble(currentInput);
                String memoryStr = "M = " + memory.toString();
                memoryScreen.setText(memoryStr);
            }
        });

        btnMSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory -= Double.parseDouble(currentInput);
                String memoryStr = "M = " + memory.toString();
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
                currentInput = memory.toString();
                inputToDisplay = previousInputs + currentInput;
                inputScreen.setText(inputToDisplay);
                if(hasOperator) {
                    outputScreen.setText(calculate());
                }
            }
        });
    }

    private void setClearOnClickListener() {
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInput = "0";
                currentNumDigits = 1;
                previousInputs = "";
                inputToDisplay = "";
                inputScreen.setText("0");
                outputScreen.setText("");
                memory = 0d;
                memoryScreen.setText("M = 0");
                hasOperator = false;
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
                lastIsOperator = false;
                if(currentInput.charAt(0)=='-'){
                    currentInput = currentInput.substring(1,currentInput.length());
                } else {
                    currentInput = "-" + currentInput;
                }
                inputToDisplay = previousInputs + currentInput;
                inputScreen.setText(inputToDisplay);
                if(hasOperator) {
                    outputScreen.setText(calculate());
                }
            }
        });
    }

    private void setDecimalOnClickListener() {
        btnDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastIsOperator = false;
                if(currentInput.contains(".")) {
                    return;
                }
                if(lastIsMR || lastIsEqual) {
                    lastIsMR = false;
                    lastIsEqual = false;
                    currentInput = "0.";
                    currentNumDigits = 1;
                } else {
                    currentInput += ".";
                }
                inputToDisplay = previousInputs + currentInput;
                inputScreen.setText(inputToDisplay);
                if(hasOperator) {
                    outputScreen.setText(calculate());
                }
            }
        });
    }

    private void setEqualOnClickListener() {
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasOperator) {
                    String result = calculate();
                    currentInput = result;
                    lastIsEqual = true;
                    outputScreen.setText(result);
                }
            }
        });
    }

    private String calculate () {
        String txt = inputScreen.getText().toString();
        Expression expression = new ExpressionBuilder(txt).build();
        try {
            double result = expression.evaluate();
            return Double.toString(result);
        } catch (ArithmeticException ex) {
            return "Error";
        }
    }


}
