package com.example.david2.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    //define max digits of input allowed
    private final int MAX_DIGITS = 10;
    //unicode strings for math symbols, same as in strings.xml
    private final String ADD = "\u002b";
    private final String SUBTRACT = "\u2212";
    private final String MULTIPLY = "\u00D7";
    private final String DIVIDE = "\u00F7";

    //current input number, is the right-side operand in calculation
    private String currentOperand = "0";
    //count digits of input number
    private int currentNumDigits = 1;
    //previous input number, is the left-side operand in calculation
    private String previousOperand = "";
    //the math operator, + - * /
    private String operator = null;
    private Double memory = 0d;
    //booleans for last pressed button
    private boolean lastIsOperator = false;
    private boolean lastIsEqual = false;
    private boolean lastIsMR = false;

    //UI elements, number buttons and operator buttons are accessed in array of IDs, because they share similar OnClickListeners
    private TextView inputOutputScreen;
    private TextView memoryScreen;
    private TextView operatorScreen;
    private int[] numberButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};
    private Button btnMC, btnMAdd, btnMSub, btnMR;
    private Button btnClear, btnSign, btnDecimal, btnEqual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link the UI elements
        inputOutputScreen = findViewById(R.id.inputOutputScreen);
        inputOutputScreen.setText("0");
        memoryScreen = findViewById(R.id.memoryScreen);
        memoryScreen.setText("M = 0");
        operatorScreen = findViewById(R.id.operatorScreen);
        btnMC = findViewById(R.id.btnMC);
        btnMAdd = findViewById(R.id.btnMAdd);
        btnMSub = findViewById(R.id.btnMSub);
        btnMR = findViewById(R.id.btnMR);
        btnClear = findViewById(R.id.btnClear);
        btnSign = findViewById(R.id.btnSign);
        btnDecimal = findViewById(R.id.btnDecimal);
        btnEqual = findViewById(R.id.btnEqual);

        //OnClickListeners defined in different methods
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
                //after an operator or equal or memory read, input should be restarted by clicking a number
                // so answer123456 doesn't happen
                if(lastIsOperator || lastIsEqual || lastIsMR) {
                    currentOperand = btnNum.getText().toString();
                    currentNumDigits = 1;
                    lastIsOperator = false;
                    lastIsEqual = false;
                    lastIsMR = false;
                } //not allow users to enter digits more than MAX_DIGITS
                else if(currentNumDigits == MAX_DIGITS) {
                    Toast.makeText(MainActivity.this, "Only 10 digits can be entered.", Toast.LENGTH_SHORT).show();
                    return;
                } //prevent users to enter 00000001, prevent leading zeros
                else {
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
        //set listener for all number buttons
        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(numberListener);
        }
    }

    //define and set OnClickListener for operators + - * /
    private void setOperatorOnClickListener() {

        View.OnClickListener operatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnOp = (Button) v;
                //prevents users enter consecutive operators
                // consecutive operator replaces the previous operator
                if(lastIsOperator) {
                    operator = btnOp.getText().toString();
                } else {
                    lastIsOperator = true;
                    //having a previous operator means users entered a op b op,
                    // need to calculate a op b and displays it
                    if(operator == null) {
                        operator = btnOp.getText().toString();
                    } else {
                        String result = calculate();
                        currentOperand = result;
                        inputOutputScreen.setText(result);
                        operator = btnOp.getText().toString();
                    }
                    //after entering operator, current operand becomes previous operand
                    previousOperand = currentOperand;
                }
                operatorScreen.setText(operator);
            }
        };
        //set listener for all operator buttons
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(operatorListener);
        }
    }

    //for all 4 memory buttons
    private void setMemoryOnClickListener() {
        //clear memory
        btnMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory = 0d;
                memoryScreen.setText("M = 0");
            }
        });

        //add current displayed value to memory
        btnMAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory += Double.parseDouble(currentOperand);
                String memoryStr = "M = " + removeTrailingZeros(memory);
                memoryScreen.setText(memoryStr);
            }
        });

        //subtract current displayed value from memory
        btnMSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory -= Double.parseDouble(currentOperand);
                String memoryStr = "M = " + removeTrailingZeros(memory);
                memoryScreen.setText(memoryStr);
            }
        });

        //replace current value with memory value
        // keep pressing MR, nothing happens
        btnMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastIsOperator = false;
                lastIsEqual = false;
                if(lastIsMR) {
                    return;
                }
                lastIsMR = true;
                currentOperand = removeTrailingZeros(memory);
                inputOutputScreen.setText(currentOperand);
            }
        });
    }

    //clear button, simply reset values to initial status
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
                operatorScreen.setText("");
                lastIsOperator = false;
                lastIsMR = false;
                lastIsEqual = false;
            }
        });
    }

    //Clicking sign button starts new input after an operator
    // otherwise just flip the positive negative sign
    // even if user just clicked equal or MR, should flip the sign without start new input
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

    //decimal point button
    // similar to number buttons, decimal point restart input after operator, equal or MR
    // also prevents more than 1 decimal point for any number
    private void setDecimalOnClickListener() {
        btnDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastIsOperator || lastIsEqual || lastIsMR) {
                    lastIsOperator = false;
                    lastIsEqual = false;
                    lastIsMR = false;
                    currentOperand = "0.";
                    currentNumDigits = 1;
                } else if(currentOperand.contains(".")) {
                    Toast.makeText(MainActivity.this, "Can only have 1 decimal point.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    currentOperand += ".";
                }
                inputOutputScreen.setText(currentOperand);
            }
        });
    }

    //equal button
    // prevent users from keep pressing equal button
    // don't allow users to press equal without pressing an operator
    // or right after operator
    private void setEqualOnClickListener() {
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastIsEqual) {
                    return;
                }
                if(operator == null) {
                    Toast.makeText(MainActivity.this, "Please enter an operator.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(lastIsOperator) {
                    Toast.makeText(MainActivity.this, "Please enter a number or press MR.", Toast.LENGTH_SHORT).show();
                    return;
                }
                lastIsOperator = false;
                lastIsMR = false;
                lastIsEqual = true;
                String result = calculate();
                currentOperand = result;
                inputOutputScreen.setText(result);
                operator = null;
                operatorScreen.setText("");
            }
        });
    }

    //calculate: previous operand (operator) current operand
    // deals with division by zero by showing message and NaN
    private String calculate () {
        double result = 0d;
        switch (operator) {
            case ADD:
                result = Double.parseDouble(previousOperand) + Double.parseDouble(currentOperand);
                break;
            case SUBTRACT:
                result = Double.parseDouble(previousOperand) - Double.parseDouble(currentOperand);
                break;
            case MULTIPLY:
                result = Double.parseDouble(previousOperand) * Double.parseDouble(currentOperand);
                break;
            case DIVIDE:
                if(Double.parseDouble(currentOperand) == 0) {
                    Toast.makeText(MainActivity.this, "Cannot divide by zero, please clear.", Toast.LENGTH_SHORT).show();
                    return "NaN";
                }
                result = Double.parseDouble(previousOperand) / Double.parseDouble(currentOperand);
                break;
        }
        return removeTrailingZeros(result);
    }

    //remove unwanted zeros present in double variables
    // numbers with more than 10 digits are shown in scientific notation
    private String removeTrailingZeros (Double d) {
        //numbers with abs value larger than this are converted to scientific notation
        double largestTenDigitsNum = 9999999999D;
        //numbers with abs value between this and 0 are converted to scientific notation
        double smallestTenDigitsNum = 0.000000001D;
        DecimalFormat dfScientific = new DecimalFormat("0.#########E0");
        DecimalFormat dfNormal = new DecimalFormat("0.#########");
        if(Math.abs(d) > largestTenDigitsNum || (d > 0 && Math.abs(d) < smallestTenDigitsNum) ) {
            return dfScientific.format(d);
        } else {
            return dfNormal.format(d);
        }
    }
}
