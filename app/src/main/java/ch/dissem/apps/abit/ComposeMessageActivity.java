/*
 * Copyright 2016 Christian Basler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.dissem.apps.abit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ch.dissem.apps.abit.service.Singleton;
import ch.dissem.bitmessage.entity.BitmessageAddress;
import ch.dissem.bitmessage.entity.Plaintext;

/**
 * Compose a new message.
 */
public class ComposeMessageActivity extends AppCompatActivity {
    public static final String EXTRA_IDENTITY = "ch.dissem.abit.Message.SENDER";
    public static final String EXTRA_RECIPIENT = "ch.dissem.abit.Message.RECIPIENT";
    public static final String EXTRA_SUBJECT = "ch.dissem.abit.Message.SUBJECT";
    public static final String EXTRA_CONTENT = "ch.dissem.abit.Message.CONTENT";
    public static final String EXTRA_BROADCAST = "ch.dissem.abit.Message.IS_BROADCAST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        // Display the fragment as the main content.
        ComposeMessageFragment fragment = new ComposeMessageFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, fragment)
            .commit();
    }

    public static void launchReplyTo(Fragment fragment, Plaintext item) {
        fragment.startActivity(getReplyIntent(fragment.getActivity(), item));
    }

    public static void launchReplyTo(Activity activity, Plaintext item) {
        activity.startActivity(getReplyIntent(activity, item));
    }

    private static Intent getReplyIntent(Context ctx, Plaintext item) {
        Intent replyIntent = new Intent(ctx, ComposeMessageActivity.class);
        replyIntent.putExtra(EXTRA_RECIPIENT, item.getFrom());
        BitmessageAddress receivingIdentity = item.getTo();
        if (receivingIdentity.isChan()) {
            // I hate when people send as chan, so it won't be the default behaviour.
            replyIntent.putExtra(EXTRA_IDENTITY, Singleton.getIdentity(ctx));
        } else {
            replyIntent.putExtra(EXTRA_IDENTITY, receivingIdentity);
        }
        String prefix;
        if (item.getSubject().length() >= 3 && item.getSubject().substring(0, 3)
            .equalsIgnoreCase("RE:")) {
            prefix = "";
        } else {
            prefix = "RE: ";
        }
        replyIntent.putExtra(EXTRA_SUBJECT, prefix + item.getSubject());
        replyIntent.putExtra(EXTRA_CONTENT,
            "\n\n------------------------------------------------------\n"
                + item.getText());
        return replyIntent;
    }
}
