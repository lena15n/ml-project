package com.machinelearning;

import com.google.gson.Gson;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/result")
public class MainService {
    //MeetingsManager meetingsManager;

    public MainService() {
        //meetingsManager = new MeetingsManager();
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/accuracy")
    @Produces("text/plain")//("application/json;charset=UTF-8")
    public String getAccuracy() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(10.0);
        list.add(15.0);
        list.add(15.0);
        list.add(14.0);
        list.add(20.0);
        list.add(7.0);
        list.add(25.0);
        list.add(4.0);
        list.add(30.0);
        list.add(6.0);
        list.add(35.0);
        list.add(2.0);

        //TODO: return result of calculations
        String json = new Gson().toJson(list);

        return json;
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/precision")
    @Produces("text/plain")//("application/json;charset=UTF-8")
    public String getPrecision() {
        ArrayList<Double> list2 = new ArrayList<>();
        list2.add(10.0);
        list2.add(15.0);
        list2.add(15.0);
        list2.add(14.0);
        list2.add(20.0);
        list2.add(7.0);
        list2.add(25.0);
        list2.add(4.0);
        list2.add(30.0);
        list2.add(6.0);
        list2.add(35.0);
        list2.add(2.0);

        //TODO: return result of calculations
        String json = new Gson().toJson(list2);

        return json;
    }
}