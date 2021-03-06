package com.nerd.si.chatter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Chatter chat = new Chatter();
    String statement,response;
    EditText textInput;
    Button talkButton;
    TextView chatOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInput = (EditText) findViewById(R.id.textInput);

        talkButton = (Button) findViewById(R.id.talkButton);

        chatOutput = (TextView) findViewById(R.id.chatOutput);

        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statement = textInput.getText().toString().replace("'", "");
                try {
                    response = chat.getResponse(statement);
                } catch(StringIndexOutOfBoundsException e) {
                    response = "Oh, no! Something went wrong, your statement may have been too long.";
                } catch(RuntimeException e) {
                    response = "Oops, something went wrong with my processors";
                } catch(Exception e) {
                    response = "That's weird, something strange went wrong...";
                }

                chatOutput.setText("Chatter: " +response);

            }
        });

        textInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textInput.selectAll();
            }
        });
        textInput.setOnEditorActionListener(new DoneOnEditorActionListener());
    }

}
