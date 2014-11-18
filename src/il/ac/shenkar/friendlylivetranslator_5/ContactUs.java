package il.ac.shenkar.friendlylivetranslator_5;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class ContactUs extends Activity 
{	 
	private String message;
	private EditText nameField;
	private Spinner feedbackSpinner;
	private EditText feedbackField;
	private CheckBox responseCheckbox;
	private Button btnSendEmail;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);
    	
        nameField = (EditText) findViewById(R.id.EditTextName);
        feedbackSpinner = (Spinner) findViewById(R.id.SpinnerFeedbackType);
        feedbackField = (EditText) findViewById(R.id.EditTextFeedbackBody);
        responseCheckbox = (CheckBox) findViewById(R.id.CheckBoxResponse);
        btnSendEmail = (Button) findViewById(R.id.ButtonSendFeedback);

		btnSendEmail.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if (responseCheckbox.isChecked())
				{
					message = "-- " + getResources().getString(R.string.feedbackmessagebody_responseyes) + " --" + "\n\n" + feedbackField.getText().toString();
				}
				else
				{
					message = "-- " + getResources().getString(R.string.feedbackmessagebody_responseno) + " --" + "\n\n" + feedbackField.getText().toString();
				}
				String[] to = {"giliaskin@ymail.com"};
                String subject = feedbackSpinner.getSelectedItem().toString() + " - from " + nameField.getText().toString();
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.putExtra(Intent.EXTRA_EMAIL,to);
                mail.putExtra(Intent.EXTRA_SUBJECT, subject);
                mail.putExtra(Intent.EXTRA_TEXT, message);
                mail.setType("message/rfc822");
                startActivity(Intent.createChooser(mail, "Send email via:"));
			}
		});
    }
}
