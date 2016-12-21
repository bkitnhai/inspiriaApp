/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.apps.topeka.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.activity.JSONParser;
import com.google.samples.apps.topeka.helper.JsonHelper;
import com.google.samples.apps.topeka.model.Category;
import com.google.samples.apps.topeka.model.JsonAttributes;
import com.google.samples.apps.topeka.model.Theme;
import com.google.samples.apps.topeka.model.quiz.AlphaPickerQuiz;
import com.google.samples.apps.topeka.model.quiz.FillBlankQuiz;
import com.google.samples.apps.topeka.model.quiz.FillTwoBlanksQuiz;
import com.google.samples.apps.topeka.model.quiz.FourQuarterQuiz;
import com.google.samples.apps.topeka.model.quiz.MultiSelectQuiz;
import com.google.samples.apps.topeka.model.quiz.PickerQuiz;
import com.google.samples.apps.topeka.model.quiz.Quiz;
import com.google.samples.apps.topeka.model.quiz.SelectItemQuiz;
import com.google.samples.apps.topeka.model.quiz.ToggleTranslateQuiz;
import com.google.samples.apps.topeka.model.quiz.TrueFalseQuiz;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Database for storing and retrieving info for categories and quizzes
 */
public class TopekaDatabaseHelper extends SQLiteOpenHelper {




    private static final String TAG = "TopekaDatabaseHelper";
    private static final String DB_NAME = "topeka";
    private static final String DB_SUFFIX = ".db";
    private static final int DB_VERSION = 1;
    private static List<Category> mCategories;
    private static TopekaDatabaseHelper mInstance;
    private final Resources mResources;

    private TopekaDatabaseHelper(Context context) {
        //prevents external instance creation
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
        mResources = context.getResources();
        Log.i("TopekaDataBaseHelper", String.valueOf(context));
        Log.i("TopekaDataBaseHelper", String.valueOf(mResources));


    }

    private static TopekaDatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new TopekaDatabaseHelper(context);
        }
        return mInstance;
    }

    /**
     * Gets all categories with their quizzes.
     *
     * @param context The context this is running in.
     * @param fromDatabase <code>true</code> if a data refresh is needed, else <code>false</code>.
     * @return All categories stored in the database.
     */
    public static List<Category> getCategories(Context context, boolean fromDatabase) throws JSONException {
        if (null == mCategories || fromDatabase) {
            mCategories = loadCategories(context);
        }
        return mCategories;
    }


    private static List<Category> loadCategories(Context context) throws JSONException {

        Cursor data = TopekaDatabaseHelper.getCategoryCursor(context);
        List<Category> tmpCategories = new ArrayList<>(data.getCount());
        tmpCategories = getCategoryFromMysqlForDisplay(data.getCount());

        /*final SQLiteDatabase readableDatabase = TopekaDatabaseHelper.getReadableDatabase(context);
        do {

            // process each row of database
            //Reriwte getCategoryFromMysql
            // step 1  get array of rows from database insperia
            // step 2 loop while to get each row put into category
            // step 3 get categoryID for quizzes to put into category
            // step 4 put category to tmpCategories
            // step 5 loop till end

            final Category category = getCategory(data, readableDatabase);//(data, readableDatabase);

            Log.i("checkCategory", String.valueOf(category));
            tmpCategories.add(category);
        } while (data.moveToNext());*/
        return tmpCategories;
    }
    private static JSONArray AllCatelogeArray = null;
    private static JSONArray AllQuizArray = null;
    private static List<Category> getCategoryFromMysqlForDisplay(int getCount) throws JSONException {
        // LoaALLQuiz for furture
        if (AllQuizArray==null){
            new LoadAllQuiz().execute();
            while (Flagcheck0 == null){};Flagcheck0 = null;
            AllQuizArray= checkSuccess(AllQuiz,"quiz");
            //
        }
        List<Category> tmpCategories = new ArrayList<>(getCount);
        if (AllCatelogeArray==null) {
            new LoadAllCategory().execute();
            while (Flagcheck4 == null) { };Flagcheck4 = null;
            AllCatelogeArray = checkSuccess(CatelogeJson, "insperia");
        }
           // JSONArray jsonArrayAllCateloge =  AllCateloge;//jsonArrayTest = [objEct1, object2,...]
        JSONObject AllCategoryObject = null;
        for (int i = 0; i < AllCatelogeArray.length(); i++) {
            AllCategoryObject = AllCatelogeArray.getJSONObject(i);

            final String id = AllCategoryObject.getString("id");
            final String name = AllCategoryObject.getString("name");
            final String themeName = AllCategoryObject.getString("theme");
            final Theme theme = Theme.valueOf(themeName);
            final String isSolved = AllCategoryObject.getString("solved");
            final boolean solved = getBooleanFromDatabase(isSolved);
            final int[] scores= JsonHelper.jsonArrayToIntArray(AllCategoryObject.getString("scores"));//get4);//cursor.getString(4));

            // call quiz database from mysql
            final List<Quiz> quizzes = new ArrayList<>();//= getQuizzes(id, readableDatabase);
            final List<String> macs = new ArrayList<>();



            for (int k =0; k < AllQuizArray.length(); k++){
               JSONObject temObject = AllQuizArray.getJSONObject(k);
                if (temObject.getString("fk_category").equals(id)){
                    quizzes.add(createQuizBaseOndatabase(temObject));
                    macs.add(temObject.getString("mac"));
                }
            }
           Category temp = new Category(name, id, macs, theme, quizzes, scores, solved);
            tmpCategories.add(temp);
        }
       return tmpCategories;
    }
    /**
     * Gets all categories wrapped in a {@link Cursor} positioned at it's first element.
     * <p>There are <b>no quizzes</b> within the categories obtained from this cursor</p>
     *
     * @param context The context this is running in.
     * @return All categories stored in the database.
     */
    private static Cursor getCategoryCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        Cursor data = readableDatabase
                .query(CategoryTable.NAME, CategoryTable.PROJECTION, null, null, null, null, null);
        data.moveToFirst();
        return data;
    }

    // url to get all products list
    private static  String Flagcheck0 = null;
    private static JSONObject AllQuiz;
    private static String urlAllQuiz = "http://oslophone.com/test/android_connect/quiz/get_all_products.php";
    static class LoadAllQuiz extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Creating JSON Parser object
            JSONParser jParser = new JSONParser();
            // getting JSON string from URL
            AllQuiz = jParser.makeHttpRequest(urlAllQuiz, "GET", params);
            Log.i("LoadMySQL C", String.valueOf(AllQuiz));
            // Check your log cat for JSON reponse
            Flagcheck0 = "ok";
            return null;
        }
    }
    // url to get all products list
    private static  String Flagcheck4 = null;
    private static JSONObject CatelogeJson;
    private static String urlForCategory = "http://oslophone.com/test/android_connect/get_all_products.php";
    static class LoadAllCategory extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Creating JSON Parser object
            JSONParser jParser = new JSONParser();
            // getting JSON string from URL
            CatelogeJson = jParser.makeHttpRequest(urlForCategory, "GET", params);
            Log.i("LoadMySQL C", String.valueOf(CatelogeJson));
            // Check your log cat for JSON reponse
            Flagcheck4 = "ok";
            return null;
        }
    }

   /* // single product url
    private static  String Flagcheck1 = null;
    private static  String TargetCategory = "food";
    private static final String url_Category = "http://nguyenhainorway.netau.net/android_connect/get_product_details.php";
    private static  JSONObject specificCategory;
    static class LoadSpeicicCategoryFromMySQL extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", TargetCategory));
            // Creating JSON Parser object
            JSONParser jParser2 = new JSONParser();
            // getting JSON string from URL
            specificCategory = jParser2.makeHttpRequest(url_Category, "GET", params);
            // Check your log cat for JSON reponse
            Log.i("LoadMySQL SC", String.valueOf(specificCategory));
            Flagcheck1 = "ok";
            return null;
        }
    }
    // single product url
    private static  String Flagcheck3 = null;
    private static  String TargetQuiz = "food";
    private static final String url_product_detials = "http://nguyenhainorway.netau.net/" +
            "" +
            "android_connect/quiz/get_product_details.php";
    private static  JSONObject specificQuiz;
    static class LoadSpeicicQuizFromMySQL extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fk_category", TargetQuiz));
            // Creating JSON Parser object
            JSONParser jParser2 = new JSONParser();
            // getting JSON string from URL
            specificQuiz = jParser2.makeHttpRequest(url_product_detials, "GET", params);
            // Check your log cat for JSON reponse
            Log.i("LoadMySQL SQ", String.valueOf(specificQuiz));
            Flagcheck3 = "ok";
            return null;
        }
    }

*/
/*
    private static Category getCategory(Cursor cursor, SQLiteDatabase readableDatabase) {
        // "magic numbers" based on CategoryTable#PROJECTION
        final String id = cursor.getString(0);//get0;//cursor.getString(0);

        final String name = cursor.getString(1);//get1;//cursor.getString(1);
        final String themeName = cursor.getString(2);//get2;//cursor.getString(2);
        final Theme theme = Theme.valueOf(themeName);
        final String isSolved = cursor.getString(3);
        final boolean solved = getBooleanFromDatabase(isSolved);
        final int[] scores = JsonHelper.jsonArrayToIntArray(cursor.getString(4));//get4);//cursor.getString(4));
        Log.i("getCategory, GET mac", id);
        // call quiz database from mysql
        final List<Quiz> quizzes = getQuizzes(id, readableDatabase);
        Log.i("getCategory, GET quizzes", String.valueOf(quizzes));
        final List<String> macs = getMAC(id, readableDatabase);
//        Log.i("getCategory, GET 5", cursor.getString(4));
        Log.i("getCategory, GET 0", cursor.getString(0));
        Log.i("getCategory, GET 1", cursor.getString(1));
        Log.i("getCategory, GET 2", cursor.getString(2));
        Log.i("getCategory, GET 3", cursor.getString(3));
        Log.i("getCategory, GET 4", cursor.getString(4));
        Log.i("getCategory, GET 5", String.valueOf(cursor.getColumnCount()));
        Log.i("getCategory, GET 6", String.valueOf(quizzes));

        //  final String mac = cursor.getString(5);
        //Log.i("getCategory", mac);
        return new Category(name, id, macs, theme, quizzes, scores, solved);
    }
*/
    private static boolean getBooleanFromDatabase(String isSolved) {
        // json stores booleans as true/false strings, whereas SQLite stores them as 0/1 values
        return null != isSolved && isSolved.length() == 1 && Integer.valueOf(isSolved) == 1;
    }

    /**
     * Looks for a category with a given id.
     *
     * @param context The context this is running in.
     * @param categoryId Id of the category to look for.
     * @return The found category.
     */
    public static Category getCategoryWith(Context context, String categoryId) throws JSONException {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {categoryId};
        // load mysql here
        // that we shoudl load specific group of database , such as food
        Cursor data = readableDatabase
                .query(CategoryTable.NAME, CategoryTable.PROJECTION, CategoryTable.COLUMN_ID + "=?",
                        selectionArgs, null, null, null);
        data.moveToFirst();
        return getCategoryFromMysql(categoryId);
        //return getCategory(data, readableDatabase);
    }

    private static JSONArray checkSuccess(JSONObject Objectjson, String TargetDatabase) {
        try {
            // Checking for SUCCESS TAG
            int success = Objectjson.getInt("success");
            if (success == 1) {
                // Getting Array of Products
                return Objectjson.getJSONArray(TargetDatabase);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONArray groupQuiz = null;
    private static Category getCategoryFromMysql(String categoryId) throws JSONException {
        // load databse fOr catagory
        JSONObject categoryObject = null;
        for (int k =0; k < AllCatelogeArray.length(); k++){
            JSONObject temObject = AllCatelogeArray.getJSONObject(k);
            if (temObject.getString("id").equals(categoryId)){
                categoryObject = temObject;
            }
        }
        final String id = categoryObject.getString("id");
        final String name = categoryObject.getString("name");
        final String themeName = categoryObject.getString("theme");
        final Theme theme = Theme.valueOf(themeName);
        final String isSolved = categoryObject.getString("solved");
        final boolean solved = getBooleanFromDatabase(isSolved);
        final int[] scores= JsonHelper.jsonArrayToIntArray(categoryObject.getString("scores"));//get4);//cursor.getString(4));

        // call quiz database from mysql
        final List<Quiz> quizzes = new ArrayList<>();//= getQuizzes(id, readableDatabase);
        final List<String> macs = new ArrayList<>();
        //load database for specific quiz
        for (int k =0; k < AllQuizArray.length(); k++){
            JSONObject temObject = AllQuizArray.getJSONObject(k);
            if (temObject.getString("fk_category").equals(categoryId)){
                quizzes.add(createQuizBaseOndatabase(temObject));
                macs.add(temObject.getString("mac"));
            }
        }

        return new Category(name, id, macs, theme, quizzes, scores, solved);
    }
    private static Quiz createQuizBaseOndatabase(JSONObject categoryObject) throws JSONException {


        // "magic numbers" based on QuizTable#PROJECTION
        String type = null;
        if(categoryObject.getString("type") != null) {
            type = categoryObject.getString("type");
        }
         String question= null;
        if(categoryObject.getString("question") != null) {
            question = categoryObject.getString("question");
        }
         String answer= null;
        if(categoryObject.getString("answer") != null) {
            answer = categoryObject.getString("answer");
        }
         String options= null;
        if(categoryObject.getString("options") != null) {
            options = categoryObject.getString("options");
        }

         boolean solved= false;
        if(categoryObject.getString("solved") != null) {
             solved = getBooleanFromDatabase(categoryObject.getString("solved"));
        }
         int min = categoryObject.getInt("min");
         int max = categoryObject.getInt("max");
         int step = categoryObject.getInt("max");


        switch (type) {
            case JsonAttributes.QuizType.ALPHA_PICKER: {
                return new AlphaPickerQuiz(question, answer, solved);
            }
            case JsonAttributes.QuizType.FILL_BLANK: {
                return createFillBlankQuizForMysql(categoryObject, question, answer, solved);
            }
            case JsonAttributes.QuizType.FILL_TWO_BLANKS: {
                return createFillTwoBlanksQuiz(question, answer, solved);
            }
            case JsonAttributes.QuizType.FOUR_QUARTER: {
                Log.i("createQuizBaseOndatabase question",question);
                Log.i("createQuizBaseOndatabase answer",answer);
                Log.i("createQuizBaseOndatabase options", options);
                Log.i("createQuizBaseOndatabase solved", String.valueOf(solved));
                Quiz a = createFourQuarterQuiz(question, answer, options, solved);
                Log.i("createQuizBaseOndatabase a", String.valueOf(a));
                return a;
            }
            case JsonAttributes.QuizType.MULTI_SELECT: {
                return createMultiSelectQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.PICKER: {
                return new PickerQuiz(question, Integer.valueOf(answer), min, max, step, solved);
            }
            case JsonAttributes.QuizType.SINGLE_SELECT:
                //fall-through intended
            case JsonAttributes.QuizType.SINGLE_SELECT_ITEM: {
                return createSelectItemQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.TOGGLE_TRANSLATE: {
                return createToggleTranslateQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.TRUE_FALSE: {
                return createTrueFalseQuiz(question, answer, solved);

            }
            default: {
                throw new IllegalArgumentException("Quiz type " + type + " is not supported");
            }
        }
    }
    private static Quiz createFillBlankQuizForMysql(JSONObject categoryObject, String question, String answer,
                                            boolean solved) throws JSONException {
        final String start = categoryObject.getString("start");
        final String end  = categoryObject.getString("end");
        return new FillBlankQuiz(question, answer, start, end, solved);
    }
    /**
     * Scooooooooooore!
     *
     * @param context The context this is running in.
     * @return The score over all Categories.
     */
    public static int getScore(Context context) throws JSONException {
        final List<Category> categories = getCategories(context, false);
        int score = 0;
        for (Category cat : categories) {
            score += cat.getScore();
        }
        return score;
    }

    /**
     * Updates values for a category.
     *
     * @param context The context this is running in.
     * @param category The category to update.
     */
    public static void updateCategory(Context context, Category category) {
        if (mCategories != null && mCategories.contains(category)) {
            final int location = mCategories.indexOf(category);
            mCategories.remove(location);
            mCategories.add(location, category);
        }
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        ContentValues categoryValues = createContentValuesFor(category);
        writableDatabase.update(CategoryTable.NAME, categoryValues, CategoryTable.COLUMN_ID + "=?",
                new String[]{category.getId()});
        final List<Quiz> quizzes = category.getQuizzes();
        updateQuizzes(writableDatabase, quizzes);
    }

    /**
     * Updates a list of given quizzes.
     *
     * @param writableDatabase The database to write the quizzes to.
     * @param quizzes The quizzes to write.
     */
    private static void updateQuizzes(SQLiteDatabase writableDatabase, List<Quiz> quizzes) {
        Quiz quiz;
        ContentValues quizValues = new ContentValues();
        String[] quizArgs = new String[1];
        for (int i = 0; i < quizzes.size(); i++) {
            quiz = quizzes.get(i);
            quizValues.clear();
            quizValues.put(QuizTable.COLUMN_SOLVED, quiz.isSolved());

            quizArgs[0] = quiz.getQuestion();
            writableDatabase.update(QuizTable.NAME, quizValues, QuizTable.COLUMN_QUESTION + "=?",
                    quizArgs);
        }
    }

    /**
     * Resets the contents of Topeka's database to it's initial state.
     *
     * @param context The context this is running in.
     */
    public static void reset(Context context) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        writableDatabase.delete(CategoryTable.NAME, null, null);
        writableDatabase.delete(QuizTable.NAME, null, null);
        getInstance(context).preFillDatabase(writableDatabase);
    }

    /**
     * Creates objects for quizzes according to a category id.
     *
     * @param categoryId The category to create quizzes for.
     * @param database The database containing the quizzes.
     * @return The found quizzes or an empty list if none were available.
     */
    private static List<Quiz> getQuizzes(final String categoryId, SQLiteDatabase database) {
        final List<Quiz> quizzes = new ArrayList<>();
        Log.i("getQuizzesCategory", categoryId);
        Log.i("getQuizzesCategory", QuizTable.NAME);
        /*query(String table, String[] columns, String selection,
        String[] selectionArgs, String groupBy, String having, String orderBy)*/


        final Cursor cursor = database.query(QuizTable.NAME, QuizTable.PROJECTION,
                QuizTable.FK_CATEGORY + " LIKE ?", new String[]{categoryId}, null, null, null);
                  cursor.moveToFirst();
        Log.i("cursor", String.valueOf(cursor));
        do {
            quizzes.add(createQuizDueToType(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return quizzes;
    }
    private static List<String> getMAC(final String categoryId, SQLiteDatabase database) {
        final List<String> macs = new ArrayList<>();
        final Cursor cursor = database.query(QuizTable.NAME, QuizTable.PROJECTION,
                QuizTable.FK_CATEGORY + " LIKE ?", new String[]{categoryId}, null, null, null);
        cursor.moveToFirst();
        do {
            final String mac = cursor.getString(12);
           macs.add(mac);
           // Log.i("Mac", String.valueOf(Integer.parseInt(cursor.getString(12))));


        } while (cursor.moveToNext());
        cursor.close();
        return macs;
    }
    /**
     * Creates a quiz corresponding to the projection provided from a cursor row.
     * Currently only {@link QuizTable#PROJECTION} is supported.
     *
     * @param cursor The Cursor containing the data.
     * @return The created quiz.
     */
    private static Quiz createQuizDueToType(Cursor cursor) {


        // "magic numbers" based on QuizTable#PROJECTION

        final String type = cursor.getString(2);
        final String question = cursor.getString(3);
        final String answer = cursor.getString(4);
        final String options = cursor.getString(5);
        final int min = cursor.getInt(6);
        final int max = cursor.getInt(7);
        final int step = cursor.getInt(8);
        final boolean solved = getBooleanFromDatabase(cursor.getString(11));
        if(cursor.getString(5) != null) {
            Log.i("createQuizDueToType options", cursor.getString(5));
        }
        Log.i("createQuizDueToType solveds", String.valueOf(solved));
        switch (type) {
            case JsonAttributes.QuizType.ALPHA_PICKER: {
                return new AlphaPickerQuiz(question, answer, solved);
            }
            case JsonAttributes.QuizType.FILL_BLANK: {
                return createFillBlankQuiz(cursor, question, answer, solved);
            }
            case JsonAttributes.QuizType.FILL_TWO_BLANKS: {
                return createFillTwoBlanksQuiz(question, answer, solved);
            }
            case JsonAttributes.QuizType.FOUR_QUARTER: {
                return createFourQuarterQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.MULTI_SELECT: {
                return createMultiSelectQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.PICKER: {
                return new PickerQuiz(question, Integer.valueOf(answer), min, max, step, solved);
            }
            case JsonAttributes.QuizType.SINGLE_SELECT:
                //fall-through intended
            case JsonAttributes.QuizType.SINGLE_SELECT_ITEM: {
                return createSelectItemQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.TOGGLE_TRANSLATE: {
                return createToggleTranslateQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.TRUE_FALSE: {
                return createTrueFalseQuiz(question, answer, solved);

            }
            default: {
                throw new IllegalArgumentException("Quiz type " + type + " is not supported");
            }
        }
    }

    private static Quiz createFillBlankQuiz(Cursor cursor, String question, String answer,
            boolean solved) {
        final String start = cursor.getString(9);
        final String end = cursor.getString(10);
        return new FillBlankQuiz(question, answer, start, end, solved);
    }

    private static Quiz createFillTwoBlanksQuiz(String question, String answer, boolean solved) {
        final String[] answerArray = JsonHelper.jsonArrayToStringArray(answer);
        return new FillTwoBlanksQuiz(question, answerArray, solved);
    }

    private static Quiz createFourQuarterQuiz(String question, String answer, String options,
            boolean solved) {
        final int[] answerArray = JsonHelper.jsonArrayToIntArray(answer);
        final String[] optionsArray = JsonHelper.jsonArrayToStringArray(options);
        return new FourQuarterQuiz(question, answerArray, optionsArray, solved);
    }

    private static Quiz createMultiSelectQuiz(String question, String answer, String options,
            boolean solved) {
        final int[] answerArray = JsonHelper.jsonArrayToIntArray(answer);
        final String[] optionsArray = JsonHelper.jsonArrayToStringArray(options);
        return new MultiSelectQuiz(question, answerArray, optionsArray, solved);
    }

    private static Quiz createSelectItemQuiz(String question, String answer, String options,
            boolean solved) {
        final int[] answerArray = JsonHelper.jsonArrayToIntArray(answer);
        final String[] optionsArray = JsonHelper.jsonArrayToStringArray(options);
        return new SelectItemQuiz(question, answerArray, optionsArray, solved);
    }

    private static Quiz createToggleTranslateQuiz(String question, String answer, String options,
            boolean solved) {
        final int[] answerArray = JsonHelper.jsonArrayToIntArray(answer);
        final String[][] optionsArrays = extractOptionsArrays(options);
        return new ToggleTranslateQuiz(question, answerArray, optionsArrays, solved);
    }

    private static Quiz createTrueFalseQuiz(String question, String answer, boolean solved) {
    /*
     * parsing json with the potential values "true" and "false"
     * see res/raw/categories.json for reference
     */
        final boolean answerValue = "true".equals(answer);
        return new TrueFalseQuiz(question, answerValue, solved);
    }

    private static String[][] extractOptionsArrays(String options) {
        final String[] optionsLvlOne = JsonHelper.jsonArrayToStringArray(options);
        final String[][] optionsArray = new String[optionsLvlOne.length][];
        for (int i = 0; i < optionsLvlOne.length; i++) {
            optionsArray[i] = JsonHelper.jsonArrayToStringArray(optionsLvlOne[i]);
        }
        return optionsArray;
    }

    /**
     * Creates the content values to update a category in the database.
     *
     * @param category The category to update.
     * @return ContentValues containing updatable data.
     */
    private static ContentValues createContentValuesFor(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryTable.COLUMN_SOLVED, category.isSolved());
        contentValues.put(CategoryTable.COLUMN_SCORES, Arrays.toString(category.getScores()));
        return contentValues;
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * create the category table first, as quiz table has a foreign key
         * constraint on category id
         */
        db.execSQL(CategoryTable.CREATE);
        db.execSQL(QuizTable.CREATE);
        preFillDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* no-op */
    }

    private void preFillDatabase(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                fillCategoriesAndQuizzes(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "preFillDatabase", e);
        }
    }

    private void fillCategoriesAndQuizzes(SQLiteDatabase db) throws JSONException, IOException {
        ContentValues values = new ContentValues(); // reduce, reuse


        JSONArray jsonArray =  new JSONArray(readCategoriesFromResources()); // read from file
        //JSONArray jsonArrayTest =  new JSONArray(readCategoriesFromResources()); // read from file//cateloge;//
        JSONObject category;
        JSONObject category2;
        for (int i = 0; i < jsonArray.length(); i++) {
            category = jsonArray.getJSONObject(i);
            category2 = jsonArray.getJSONObject(i);

            // db is only null because it has not been initilited , it just initlited null quizz and null catelory

            Log.i("Displaycategory", String.valueOf(category));


            final String categoryId = category.getString(JsonAttributes.ID); // step1 changE mysql follow jsonfle in topeka.
            Log.i("categoryId", jsonArray.getJSONObject(i).getString(JsonAttributes.ID) );


            // category contain all of information
            //db that need to put data
            // categoryId ID that got from category database
// change category = keep, cagegoryid
            fillCategory(db, values, category, categoryId);


            // step 3 chang quzzes get data from quiz mysql database
            final JSONArray quizzes = category2.getJSONArray(JsonAttributes.QUIZZES); // get but responding to categoryID
            Log.i("quizzes", String.valueOf(quizzes));
            // quizzes that got from category
            fillQuizzesForCategory(db, values, quizzes, categoryId);
        }
    }

    private String readCategoriesFromResources() throws IOException {
        StringBuilder categoriesJson = new StringBuilder();
        InputStream rawCategories = mResources.openRawResource(R.raw.insperia);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawCategories));
        String line;

        while ((line = reader.readLine()) != null) {
            categoriesJson.append(line);
        }
        Log.i("checkCategoriesJson", categoriesJson.toString());
        return categoriesJson.toString();
    }
// code here
    private void fillCategory(SQLiteDatabase db, ContentValues values, JSONObject category,
            String categoryId) throws JSONException {
        // category contail all of information
        //db that need to put data
        // categoryId ID that got from category database
        Log.i("checkInsert",CategoryTable.COLUMN_ID + categoryId);
        Log.i("checkInsert",CategoryTable.COLUMN_NAME + category.getString(JsonAttributes.NAME));
        Log.i("checkInsert",CategoryTable.COLUMN_THEME +  category.getString(JsonAttributes.THEME));
        Log.i("checkInsert",CategoryTable.COLUMN_SOLVED + category.getString(JsonAttributes.SOLVED));
        Log.i("checkInsert",CategoryTable.COLUMN_SCORES + category.getString(JsonAttributes.SCORES));



        values.clear();
        values.put(CategoryTable.COLUMN_ID, categoryId);
        values.put(CategoryTable.COLUMN_NAME, category.getString(JsonAttributes.NAME));
        values.put(CategoryTable.COLUMN_THEME, category.getString(JsonAttributes.THEME));
        values.put(CategoryTable.COLUMN_SOLVED, category.getString(JsonAttributes.SOLVED));
        values.put(CategoryTable.COLUMN_SCORES, category.getString(JsonAttributes.SCORES));
        db.insert(CategoryTable.NAME, null, values);
    }

    private void fillQuizzesForCategory(SQLiteDatabase db, ContentValues values, JSONArray quizzes,
            String categoryId) throws JSONException {
        JSONObject quiz;
        for (int i = 0; i < quizzes.length(); i++) {
            quiz = quizzes.getJSONObject(i);
            values.clear();
            values.put(QuizTable.FK_CATEGORY, categoryId);
            values.put(QuizTable.COLUMN_TYPE, quiz.getString(JsonAttributes.TYPE));
            values.put(QuizTable.COLUMN_QUESTION, quiz.getString(JsonAttributes.QUESTION));
            values.put(QuizTable.COLUMN_ANSWER, quiz.getString(JsonAttributes.ANSWER));



            putNonEmptyString(values, quiz, JsonAttributes.OPTIONS, QuizTable.COLUMN_OPTIONS);
            putNonEmptyString(values, quiz, JsonAttributes.MIN, QuizTable.COLUMN_MIN);
            putNonEmptyString(values, quiz, JsonAttributes.MAX, QuizTable.COLUMN_MAX);
            putNonEmptyString(values, quiz, JsonAttributes.START, QuizTable.COLUMN_START);
            putNonEmptyString(values, quiz, JsonAttributes.END, QuizTable.COLUMN_END);
            putNonEmptyString(values, quiz, JsonAttributes.STEP, QuizTable.COLUMN_STEP);
            //Log.i("getIntMac", quiz.getString("mac"));
            values.put(QuizTable.COLUMN_MAC,quiz.getString("mac"));




            db.insert(QuizTable.NAME, null, values);
        }
    }

    /**
     * Puts a non-empty string to ContentValues provided.
     *
     * @param values The place where the data should be put.
     * @param quiz The quiz potentially containing the data.
     * @param jsonKey The key to look for.
     * @param contentKey The key use for placing the data in the database.
     * @throws JSONException Thrown when there's an issue with JSON.
     */
    private void putNonEmptyString(ContentValues values, JSONObject quiz, String jsonKey,
            String contentKey) throws JSONException {
        final String stringToPut = quiz.optString(jsonKey, null);
        if (!TextUtils.isEmpty(stringToPut)) {
            values.put(contentKey, stringToPut);
        }
    }

}
