package com.peluca.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.peluca.android.list.TweetAdapter;
import com.peluca.android.models.Tweet;
import com.peluca.android.utils.ConstantsUtils;
import com.peluca.android.utils.TwitterUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends Activity {

    private ListView lvTimeLine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        lvTimeLine = (ListView) findViewById(R.id.lv_timeline);

        new TweetSearchTask().execute();

    }

    public void updateListView(ArrayList<Tweet> tweets) {
        lvTimeLine.setAdapter(new TweetAdapter(this, R.layout.row_tweet, tweets));
    }

    class TweetSearchTask extends AsyncTask<Object, Void, ArrayList<Tweet>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TimelineActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_tweet_search_loader));
            progressDialog.show();
        }

        @Override
        protected ArrayList<Tweet> doInBackground(Object... objects) {

            ArrayList<Tweet> tweets = new ArrayList<Tweet>();

            try {
                String timeline = TwitterUtils.getTimelineForSearchTerm(ConstantsUtils.MEJORANDROID_TERM);
                JSONObject jsonResponse = new JSONObject(timeline);
                JSONArray jsonArray = jsonResponse.getJSONArray("statuses");
                JSONObject jsonObject;

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    Tweet tweet = new Tweet();

                    tweet.setName(jsonObject.getJSONObject("user").getString("name"));
                    tweet.setScreenName(jsonObject.getJSONObject("user").getString("screen_name"));
                    tweet.setProfileImageUrl(jsonObject.getJSONObject("user").getString("profile_image_url"));
                    tweet.setText(jsonObject.getString("text"));
                    tweet.setCreatedAt(jsonObject.getString("created_at"));

                    tweets.add(i, tweet);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return tweets;
        }

        @Override
        protected void onPostExecute(ArrayList<Tweet> tweets) {
            super.onPostExecute(tweets);
            progressDialog.dismiss();

            if (tweets.isEmpty()) {
                Toast.makeText(TimelineActivity.this,
                        getResources().getString(R.string.label_tweets_not_found),
                        Toast.LENGTH_SHORT).show();
            } else {
                updateListView(tweets);
            }
        }
    }
}
