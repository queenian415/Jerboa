package com.jebora.jebora;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jebora.jebora.Utils.FileInfo;
import com.jebora.jebora.Utils.KidInfo;
import com.jebora.jebora.Utils.OrderEntry;
import com.jebora.jebora.Utils.OrderInfo;
import com.jebora.jebora.Utils.ProductInfo;
import com.jebora.jebora.Utils.ShippingInfo;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tiffanie on 15-05-25.
 */
public class ServerCommunication {

    private static final String TAG = "ServerCommunication";

    public static String signUp(Context context, String username, String password) {
        Log.d(TAG, "signUp");

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        try {
            user.signUp();
            new UserRecorder(context, UserRecorder.NEW_USER);
            return user.getObjectId();
        } catch (ParseException e) {
            if (e.getCode() == ParseException.USERNAME_TAKEN) {
                return SignUp.SIGNUP_EXISTS;
            } else {
                Log.d(TAG, "signUp failed: " + e.getMessage());
                return SignUp.SIGNUP_ERROR;
            }
        }
    }

    public static Boolean logIn(Context context, String username, String password) {
        Log.d(TAG, "logIn");

        try {
            ParseUser.logIn(username, password);
            new UserRecorder(context, UserRecorder.RETURNED_USER);

            if (UserRecorder.isFirstTimeLogIn()) {
                // user is using a new phone
                // we want to update the kid list in this case
                UserRecorder.updateKidList(getKids());
            }
            return true;
        } catch (ParseException e) {
            Log.d(TAG, "logIn failed: " + e.getMessage());
            return false;
        }
    }

    public static void logOut() {
        ParseUser.logOut();
        UserRecorder.cleanUpThread();
    }

    public static String addKid(String kidName, String kidBirthday, String kidGender, String kidRelation) {
        Log.d(TAG, "addKid");

        // Store kid's info in Parse
        ParseObject kid = new ParseObject("Kid");
        kid.put("kidName", kidName);
        kid.put("kidBirthday", kidBirthday);
        kid.put("kidGender", kidGender);
        kid.put("kidRelation", kidRelation);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Create parent - kid relationship
            kid.put("kidParent", currentUser);
        } else {
            return SignUp_2.SIGNUP2_ERROR;
        }

        try {
            kid.save();
            String kidId = kid.getObjectId();
            UserRecorder.addOneKid(kidId, kidName);
            return kidId;
        } catch (ParseException e) {
            Log.d("addKid", e.getMessage());
            return SignUp_2.SIGNUP2_ERROR;
        }
    }

    public static boolean deleteKid(Context context, String kidId) {
        Log.d(TAG, "deleteKid");

        if (!isNetworkConnected(context))
            return false;

        ParseObject kid = ParseObject.createWithoutData("Kid", kidId);
        ParseUser user = ParseUser.getCurrentUser();
        // Delete all images associated
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("user", user);
        query.whereEqualTo("kid", kid);
        List<ParseObject> imageEntries;
        try {
            imageEntries = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "deleteKid: error: " + e.getMessage());
            return false;
        }

        if (imageEntries.size() > 0) {
            try {
                ParseObject.deleteAll(imageEntries);
            } catch (ParseException e) {
                Log.d(TAG, "deleteKid: error: " + e.getMessage());
                return false;
            }
        }

        try {
            kid.delete();
        } catch (ParseException e) {
            Log.d(TAG, "deleteKid: error: " + e.getMessage());
            return false;
        }

        UserRecorder.updateKidList(getKids());
        return true;
    }

    public static KidInfo getKidObject(String kidId) {
        Log.d(TAG, "getKidObjects");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Kid");
        try {
            ParseObject kid = query.get(kidId);

            return new KidInfo(kid.getObjectId(), kid.getString("kidBirthday"),
                    kid.getString("kidGender"), kid.getString("kidName"), kid.getString("kidRelation"));
        } catch (ParseException e) {
            Log.d("getKidObjects", e.getMessage());
            return null;
        }
    }

    public static boolean editKidObject(KidInfo kidInfo) {
        Log.d(TAG, "editKidObject");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Kid");
        try {
            ParseObject kid = query.get(kidInfo.getKidId());
            kid.put("kidBirthday", kidInfo.getKidBirthday());
            kid.put("kidGender", kidInfo.getKidGender());
            kid.put("kidName", kidInfo.getKidName());
            kid.put("kidRelation", kidInfo.getKidRelation());
            try {
                kid.save();
                UserRecorder.updateKidList(getKids());
                return true;
            } catch (ParseException e) {
                Log.d(TAG, "editKidObject: " + e.getMessage());
                return false;
            }
        } catch (ParseException e) {
            Log.d(TAG, "editKidObject: " + e.getMessage());
            return false;
        }
    }

    public static HashMap<String, String> getKids() {
        Log.d(TAG, "getKids");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Kid");
        query.whereEqualTo("kidParent", ParseUser.getCurrentUser());
        try {
            List<ParseObject> list = query.find();
            HashMap<String, String> kidList = new HashMap<>();

            for (ParseObject obj: list) {
                kidList.put(obj.getObjectId(), obj.getString("kidName"));
            }
            return kidList;

        } catch (ParseException e) {
            Log.d("getKids", e.getMessage());
            return null;
        }
    }

    public static void saveImageInBackground(Context context, final String fileFullName, final String filename, final String kidId) {
        Log.d(TAG, "saveImageInBackground");

        // TODO: Parse file limit is 10M. Decrease size if size is over limit
        if (isNetworkConnected(context)) {
            byte[] data = FileInfo.getFileByteArray(fileFullName);
            if (data == null) {
                return; // error
            }

            final ParseFile image = new ParseFile(filename, data);
            image.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Image saved successfully, now link image to user and kid
                        ParseUser currentUser = ParseUser.getCurrentUser();

                        boolean isKid = true;
                        if (kidId == null) {
                            isKid = false;
                        }

                        ParseObject parseObject = new ParseObject("Image");
                        parseObject.put("image", image);
                        parseObject.put("imageName", filename);
                        parseObject.put("user", currentUser);
                        if (isKid) {
                            parseObject.put("kid", ParseObject.createWithoutData("Kid", kidId));
                        }
                        parseObject.put("isKid", isKid);
                        parseObject.saveInBackground();
                    } else {
                        Log.d(TAG, "saveImage: " + e.getMessage() + "filename: " + filename);
                        File image = new File(fileFullName);
                        UserRecorder.addToImagesNotInServerList(image);
                    }
                }
            });
        } else {
            File image = new File(fileFullName);
            UserRecorder.addToImagesNotInServerList(image);
        }
    }

    // This method is called in threads, so it has to be synchronized.
    // If not, multiple threads may do the same thing and mess it up
    public static synchronized void syncUpImages(Context context) {
        Log.d(TAG, "syncUpImages");

        List<File> imageList = UserRecorder.getImagesNotInServerList();
        if (isNetworkConnected(context) && imageList.size() > 0) {
            for (File image : imageList) {
                // First check if image is already in the server
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.whereEqualTo("imageName", image.getName());
                try {
                    query.getFirst();
                    // Image found in the server. Remove image without saving
                    UserRecorder.deleteFromImagesNotInServerList(image);
                } catch (ParseException e) {
                    // Image is not in the server. Store it.
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        Log.d(TAG, "syncUpImages: " + image.getName() + " not in server");
                        // Retrieve the parent dir name
                        String parentDir = image.getParent();
                        String parentName = parentDir.substring(parentDir.lastIndexOf("/") + 1, parentDir.length());
                        // If parent directory's name is not userid, then it's kidid and belongs to user's kid
                        if (!parentName.equals(UserRecorder.getUserId())) {
                            saveImageInBackground(context, image.getAbsolutePath(), image.getName(), parentName);
                        } else {
                            saveImageInBackground(context, image.getAbsolutePath(), image.getName(), null);
                        }
                        UserRecorder.deleteFromImagesNotInServerList(image);
                    } else {
                        Log.d("syncUpImages", e.getMessage());
                    }
                }
            }
        }
    }

    // Only used when first log in
    public static void syncDownImages(Context context, int numOfImages) {
        Log.d(TAG, "syncDownImages");

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("user", currentUser);
        query.setLimit(numOfImages);

        try {
            List<ParseObject> list = query.find();
            for (ParseObject object : list) {
                String imageName = object.getString("imageName");
                ParseFile image = (ParseFile) object.get("image");

                if (object.getBoolean("isKid")) {
                    String kidId = ((ParseObject)object.get("kid")).getObjectId();
                    // set preferred kid to create kid image path
                    UserRecorder.setPreferredKid(kidId);
                } else {
                    // set preferred kid to update image path
                    UserRecorder.setPreferredKid(null);
                }
                String filePath = FileInfo.getUserKidDirectory(context).toString() + File.separator + imageName;
                try {
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
                    out.write(image.getData());
                    out.close();

                    // compress images
                    Bitmap compressed = FileInfo.compressImage(new File(filePath));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    compressed.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    try {
                        OutputStream outputStream = new FileOutputStream(
                                FileInfo.getUserKidCompressedDirectory(context).getAbsolutePath() + File.separator + imageName);
                        bos.writeTo(outputStream);
                        bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            Log.d("loadImages", e.getMessage());
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected());
    }

    public static void resetPassword(String email) {
        Log.d(TAG, "resetPassword");

        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // An email was successfully sent with reset instructions.
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public static ShippingInfo saveShippingInfo(ShippingInfo shippingInfo) {
        Log.d(TAG, "saveShippingInfo");

        ParseObject info = new ParseObject("ShippingInfo");
        info.put("user", ParseUser.getCurrentUser());
        info.put("name", shippingInfo.getName());
        info.put("address", shippingInfo.getAddress());
        info.put("city", shippingInfo.getCity());
        info.put("country", shippingInfo.getCountry());
        info.put("postalCode", shippingInfo.getPostalCode());

        try {
            info.save();
            shippingInfo.setObjectId(info.getObjectId());
            return shippingInfo;
        } catch (ParseException e) {
            Log.d(TAG, "saveShippingInfo: error: " + e.getMessage());
            return null;
        }
    }

    public static void editShippingInfo(final ShippingInfo shippingInfo) {
        Log.d(TAG, "editShippingInfo");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShippingInfo");
        query.getInBackground(shippingInfo.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject info, ParseException e) {
                if (e == null) {
                    info.put("name", shippingInfo.getName());
                    info.put("address", shippingInfo.getAddress());
                    info.put("city", shippingInfo.getCity());
                    info.put("country", shippingInfo.getCountry());
                    info.put("postalCode", shippingInfo.getPostalCode());

                    try {
                        info.save();
                    } catch (ParseException e2) {
                        Log.d(TAG, "editShippingInfo: error: " + e.getMessage());
                    }
                } else {
                    Log.d(TAG, "editShippingInfo: error: " + e.getMessage());
                }
            }
        });
    }

    public static List<ShippingInfo> getShippingInfoList() {
        Log.d(TAG, "getShippingInfoList");

        List<ShippingInfo> shippingInfos = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShippingInfo");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            List<ParseObject> objects = query.find();
            for (ParseObject object : objects) {
                shippingInfos.add(new ShippingInfo(object.getObjectId(), object.getString("name"),
                        object.getString("address"), object.getString("city"), object.getString("country"),
                        object.getString("postalCode")));
            }
            return shippingInfos;

        } catch (ParseException e) {
            Log.d(TAG, "getShippingInfoList: error: " + e.getMessage());
            return null;
        }
    }

    public static List<ProductInfo> getProducts() {
        Log.d(TAG, "getProduct");

        List<ProductInfo> products = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
        try {
            List<ParseObject> list = query.find();
            for (ParseObject object : list) {
                ProductInfo product = new ProductInfo(object.getObjectId(), object.getString("productName"),
                        object.getInt("price"));
                products.add(product);
            }
            return products;
        } catch (ParseException e) {
            Log.d(TAG, "getProduct: error: " + e.getMessage());
            return null;
        }
    }

    public static boolean submitOrder(OrderInfo order) {
        Log.d(TAG, "submitOrder");

        ParseObject thisOrder = new ParseObject("Order");
        thisOrder.put("user", UserRecorder.getUserId());
        thisOrder.put("shipping", ParseObject.createWithoutData("ShippingInfo", order.getShipping().getObjectId()));
        try {
            thisOrder.save();
        } catch (ParseException e) {
            Log.d(TAG, "submitOrder: error: " + e.getMessage());
            return false;
        }

        for (OrderEntry entry : order.getOrderEntries()) {
            ParseObject orderEntry = new ParseObject("OrderEntry");
            orderEntry.put("order", thisOrder.getObjectId());
            orderEntry.put("product", ParseObject.createWithoutData("Product", entry.getProduct().getProductId()));
            orderEntry.put("numOfItems", entry.getNumOfItems());
            try {
                orderEntry.save();
            } catch (ParseException e) {
                Log.d(TAG, "submitOrder: error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static List<String> loadImages() {
        Log.d(TAG, "loadImages");
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Get current kid, null if user's images
        String kidId = UserRecorder.getPreferredKidId();
        ParseObject currentKid = null;
        if (kidId != null) {
            // Get kid object
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Kid");
            try {
                currentKid = query.get(kidId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<String> imageUrl = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("user", currentUser);
        if (currentKid != null) {
            query.whereEqualTo("kid", currentKid);
        }

        try {
            List<ParseObject> list = query.find();
            for (ParseObject object : list) {
                ParseFile image = (ParseFile) object.get("image");
                imageUrl.add(image.getUrl());
            }

        } catch (ParseException e) {
            Log.d(TAG, "loadImages: " + e.getMessage());
        }
        return imageUrl;
    }
}
