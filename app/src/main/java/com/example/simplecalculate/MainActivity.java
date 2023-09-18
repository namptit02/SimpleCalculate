package com.example.simplecalculate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView calculationTextView;
    private TextView resultTextView;

    private StringBuilder calculation = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculationTextView = findViewById(R.id.calculationTextView);
        resultTextView = findViewById(R.id.resultTextView);

        // Xử lý sự kiện khi nhấn các nút số và toán tử
        Button[] numberButtons = new Button[10];
        int[] numberButtonIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = findViewById(numberButtonIds[i]);
            final int finalI = i;
            numberButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCalculation(finalI);
                }
            });
        }

        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnMulti = findViewById(R.id.btnMulti);
        Button btnDiv = findViewById(R.id.btnDiv);
        Button btnPhay = findViewById(R.id.btnPhay);
        Button btnOff = findViewById(R.id.btnOff);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCalculation("+");
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCalculation("-");
            }
        });

        btnMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCalculation("*");
            }
        });

        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCalculation("/");
            }
        });

        btnPhay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCalculation(".");
            }
        });

        Button btnBang = findViewById(R.id.btnBang);
        btnBang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult();
            }
        });

        Button btnC = findViewById(R.id.btnC);
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCalculation();
            }
        });

        Button btnXoa = findViewById(R.id.btnXoa);
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastCharacter();
            }
        });
    }

    private void addToCalculation(String value) {
        calculation.append(value);
        calculationTextView.setText(calculation.toString());
    }

    private void addToCalculation(int value) {
        calculation.append(value);
        calculationTextView.setText(calculation.toString());
    }

    private void clearCalculation() {
        calculation.setLength(0);
        calculationTextView.setText("");
        resultTextView.setText("");
    }

    private void removeLastCharacter() {
        if (calculation.length() > 0) {
            calculation.deleteCharAt(calculation.length() - 1);
            calculationTextView.setText(calculation.toString());
        }
    }

    private void calculateResult() {
        try {
            String calculationString = calculation.toString();
            double result = eval(calculationString);

            // Kiểm tra nếu kết quả là số nguyên thì hiển thị dưới dạng số nguyên, ngược lại hiển thị thập phân
            if (result == (int) result) {
                resultTextView.setText(String.valueOf((int) result));
            } else {
                resultTextView.setText(String.valueOf(result));
            }
        } catch (Exception e) {
            resultTextView.setText("Lỗi");
        }
    }


    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Lỗi cú pháp: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Lỗi cú pháp: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}
