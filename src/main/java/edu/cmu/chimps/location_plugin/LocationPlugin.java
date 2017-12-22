package edu.cmu.chimps.location_plugin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.SemanticTemplate;
import edu.cmu.chimps.messageontap_api.ServiceAttributes;
import edu.cmu.chimps.messageontap_api.Tag;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class LocationPlugin extends MessageOnTapPlugin {

    public static final String TAG = "Location plugin";
    public String SEMANTIC_TEMPLATE_LOCATION = "location";


    /**
     * Return the trigger criteria of this plug-in. This will be called when
     * MessageOnTap is started (when this plugin is already enabled) or when
     * this plugin is being enabled.
     *
     * @return PluginData containing the trigger
     */

    public void clearLists(Set<String> mMandatory, Set<String> mOptional) {
        mMandatory.clear();
        mOptional.clear();
    }

    /**
     * Return the semantic templates of this plug-in. This will be called when
     * MessageOnTap is started (when this plugin is already enabled) or when
     * this plugin is being enabled.
     *
     * @return Set set of semantic templates
     */
    @Override
    protected Set<SemanticTemplate> semanticTemplates() {
        Set<SemanticTemplate> templates = new HashSet<>();

        /*
         * Semantic template I: what are you doing.
         */
        Set<Tag> tags = new HashSet<>();
        Set<String> reSet = new HashSet<>();
        reSet.add("What");
        reSet.add("Where");
        tags.add(new Tag("tag_What", reSet, Tag.Type.MANDATORY));
        templates.add(new SemanticTemplate().name(SEMANTIC_TEMPLATE_LOCATION)
                .tags(tags)
                .direction(ParseTree.Direction.INCOMING));

        return templates;
    }

    //取得坐标，请求地址操作，返回html
    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e("locationplugin:", JSONUtils.hashMapToString(params));

        Geocoder geo = new Geocoder(this);


        final long mSid = sid;
        final HashMap<String, Object> mParams = params;


        LocationUtils.getInstance(this).showLocation().map(location -> {
            try {
                Log.e("WEI",""+location.getLongitude()+" "+location.getLatitude());
                return geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
            } catch (IOException e) {
                Log.e("geo","Error : " + e.toString());
                return null;
            }
        }).subscribeOn(Schedulers.io()).subscribe(address -> {
            Log.e(TAG,address.getAddressLine(0));
            HashMap<String, Object> temp = new HashMap<String, Object>(mParams);
            temp.put(ServiceAttributes.UI.BUBBLE_FIRST_LINE, address.getAddressLine(0));
            temp.put(ServiceAttributes.UI.BUBBLE_SECOND_LINE, "");

            createTask(mSid, MethodConstants.UI_TYPE,
                MethodConstants.UI_METHOD_SHOW_BUBBLE, temp);
        });


    }


    @Override
    protected void newTaskResponded(long sid, long tid, HashMap<String, Object> params) throws Exception {

    }


    @Override
    protected void endSession(long sid) {

    }
}


