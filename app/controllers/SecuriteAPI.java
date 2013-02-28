package controllers;

import play.mvc.Http;
import play.mvc.Result;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class SecuriteAPI extends Securite {

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return unauthorized();
    }
}