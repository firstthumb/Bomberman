package net.javaci.mobile.bomberman.core.net.appwarp;

import net.javaci.mobile.bomberman.core.net.NetworkListenerAdapter;
import net.javaci.mobile.bomberman.core.net.models.RoomModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AppWarpClientTest {
    private static AppWarpClient client;
    private static String username = "ilkinulas";
    private static CountDownLatch connectLatch = new CountDownLatch(1);
    private static CountDownLatch disconnectLatch = new CountDownLatch(1);
/*
    @BeforeClass
    public static void beforeClass() throws Exception {

        client = new AppWarpClient(username);
        client.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onConnected() {
                connectLatch.countDown();
            }

            @Override
            public void onDisconnected() {
                disconnectLatch.countDown();
            }
        });
        client.connect();
        assertTrue(connectLatch.await(10, TimeUnit.SECONDS));
    }

    @AfterClass
    public static void afterClass() throws Exception {
        deleteAllRooms();
        client.disconnect();
        assertTrue(disconnectLatch.await(10, TimeUnit.SECONDS));
    }

    public static void deleteAllRooms() throws Exception {
        final CountDownLatch roomListLatch = new CountDownLatch(1);
        client.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onRoomListReceived(List<RoomModel> rooms) {
                for (RoomModel room : rooms) {
                    System.out.println("Deleting room " + room.getId());
                    client.deleteRoom(room.getId());
                }
                roomListLatch.countDown();
            }

            @Override
            public void onRoomDeleted(String roomId) {
                System.out.println("Room " + roomId + " deleted");
            }
        });
        client.listRooms();
        assertTrue(roomListLatch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testCreateRoom() throws Exception {
        String roomName = "fikirton_test_room_" + Math.random();
        createRoom(roomName);
    }

    private void createRoom(String roomName) throws InterruptedException {
        final CountDownLatch createRoomLatch = new CountDownLatch(1);
        client.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onRoomCreated(RoomModel room) {
                createRoomLatch.countDown();
            }
        });

        client.createRoom(roomName);
        assertTrue(createRoomLatch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testListRooms() throws Exception {
        deleteAllRooms();
        createRoom("test_room_1");
        createRoom("test_room_2");
        createRoom("test_room_3");

        final CountDownLatch latch = new CountDownLatch(1);
        final int [] numRooms = {0};
        client.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onRoomListReceived(List<RoomModel> rooms) {
                numRooms[0] = rooms.size();
                latch.countDown();

            }
        });
        client.listRooms();
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertEquals(3, numRooms[0]);
    }

    @Test
    public void testJoinRoom() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        client.addNetworkListener(new NetworkListenerAdapter() {
            @Override
            public void onRoomCreated(RoomModel room) {
                client.joinRoom(room.getId());
            }

            @Override
            public void onJoinRoomSuccess(String roomId) {
                latch.countDown();
            }
        });
        createRoom("test_room_1");
        assertTrue(latch.await(10, TimeUnit.SECONDS));

    }



    @Test
    public void createRoom() throws Exception {

        final CountDownLatch createRoomLatch = new CountDownLatch(1);
        final String roomName = "Fikirton_test_room_" + Math.random();
        final String username = "ilkinulas";
        this.client = new AppWarpClient(username, new NetworkListenerAdapter() {
            @Override
            public void onConnected() {
                sendCreateRoomRequest(roomName);
            }

            @Override
            public void onRoomCreated(RoomModel room) {
                assertEquals(username, room.getOwner());
                assertEquals(roomName, room.getName());
                createRoomLatch.countDown();
            }
        });
        client.connect();
        assertTrue(createRoomLatch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void listRooms() throws Exception {
        final CountDownLatch roomListLatch = new CountDownLatch(1);
        this.client = new AppWarpClient("ilkinulas", new NetworkListenerAdapter() {
            @Override
            public void onConnected() {
                sendListRoomsRequest();
            }

            @Override
            public void onRoomListReceived(List<RoomModel> rooms) {
                roomListLatch.countDown();
                System.out.println("Room List : ");
                for (RoomModel room : rooms) {
                    System.out.println(room);
                }
            }
        });

        client.connect();
        assertTrue(roomListLatch.await(10, TimeUnit.SECONDS));

        client.disconnect();
    }

    */

}
