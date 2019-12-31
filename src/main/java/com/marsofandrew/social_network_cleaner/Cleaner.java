/**
 * Created by marsofandrew (Andrew Petrov)
 * <p>
 * 2019-12-30
 */

package com.marsofandrew.social_network_cleaner;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.wall.WallGetFilter;

import java.util.*;
import java.util.function.Predicate;


public class Cleaner {
    private final UserActor userActor;
    private final VkApiClient vkApiClient;

    public Cleaner(UserActor userActor) {
        this.userActor = userActor;
        TransportClient transportClient = HttpTransportClient.getInstance();
        vkApiClient = new VkApiClient(transportClient);
    }

    public boolean cleanWall(Predicate<? super Map.Entry<Integer, Integer>> predicate, WallGetFilter filter) {
        Map<Integer, Integer> map = new HashMap<>();
        try {


            GetResponse response = vkApiClient.wall()
                    .get(userActor)
                    .filter(filter)
                    .execute();

            for (int i = 0; i < response.getCount(); i += 100) {
                GetResponse response1 = vkApiClient.wall()
                        .get(userActor)
                        .filter(filter)
                        .offset(i)
                        .execute();
                response1.getItems()
                        .stream()
                        .forEach(wallPostFull ->
                                         map.put(wallPostFull.getId(), wallPostFull.getDate()));

            }

            Set<Integer> ids = new HashSet<>();
            map.entrySet()
                    .stream()
                    .filter(predicate)
                    .forEach(entry -> ids.add(entry.getKey()));

            System.out.printf("%d posts will be deleted", ids.size());
            for (Integer id : ids) {
                vkApiClient.wall()
                        .delete(userActor)
                        .postId(id)
                        .execute();
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
