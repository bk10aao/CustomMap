import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomMapTest {

    @Test
    public void createEmptyMap_returnsMapOfSize_0() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_onPutKeyOf_null_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertThrows(NullPointerException.class, ()-> map.put(null, 10));
    }


    @Test
    public void createMap_andAddOneItem_returnsSizeOf_1() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_andAddTwoItems_ofSameValue_returnsSizeOf_1() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertEquals(1, map.size());
        Object previous = map.put("abc", "def");
        assertNotNull(previous);

        assertEquals("def", previous);
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_andAddTwoItems_returnsSizeOf_2() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertNull(map.put("def", "ghi"));
        assertEquals(2, map.size());
    }

    @Test
    public void createMap_withNoValues_onGetKey_abc_returns_null() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.get("abc"));
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_ghi_returns_null() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("ghi");
        assertNull(value);
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_abc_returns_def() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
    }

    @Test
    public void createMap_addTwoItems_returnsSizeOf_2_andMatchingKeyValuePairs() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
        assertNull(map.put("ghi", "jkl"));
        value = map.get("ghi");
        assertEquals(value, "jkl");
    }

    @Test
    public void onCreatingMapOfKeyValue_String_String_onAddingEntryOf_Integer_Integer_throw_IllegalArgumentException() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertEquals(1, map.size());
        assertThrows(IllegalArgumentException.class, ()-> map.put(10, 10));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_put_123_789_returnsPreviousValue_456_andUpdatesKey_123_to_789() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(value, 456);

        Object previous = map.put("123", 789);
        assertNotNull(previous);
        assertEquals(456, previous);
        Object updatedValue = map.get("123");
        assertNotEquals(456, updatedValue);
        assertEquals(789, updatedValue);
    }

    @Test
    public void createMapOfKeyValue_String_String_onRemoveKeyOfNull_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, String.class);
        assertThrows(NullPointerException.class, ()-> map.remove(null));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_removeKeyOf_123_returnsKeysValue_456_andSizeOf_0_andNoLongerContainsKey_123() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);

        value = map.remove("123");
        assertEquals(456, value);
        assertEquals(0, map.size());

        value = map.get("123");
        assertNull(value);
        assertFalse(map.containsKey("123"));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_containsKeyOf_Null_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.get(null));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_removeKeyOf_789_returnsNull_andSizeOf_1() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);

        value = map.remove("789");
        assertNull(value);
        assertEquals(1, map.size());
    }

    @Test
    public void givenMap_onPutIfAbsent_withNullKey_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.putIfAbsent(null, 123));
    }

    @Test
    public void givenMap_onPutIfAbsent_withNullValue_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.putIfAbsent("123", null));
    }

    @Test
    public void givenMap_onPutIfAbsent_withNullKey_andNullValue_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.putIfAbsent(null, null));
    }

    @Test
    public void givenMap_onPutIfAbsentKeyThatExists_returnsExistingValue() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        map.put("123", 456);
        assertEquals(456, map.putIfAbsent("123", 456));
    }

    @Test
    public void givenMap_onPutIfAbsentKeyThatDoesNotExist_returnsNull() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.putIfAbsent("123", 456));
        assertEquals(456, map.get("123"));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullKey_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.remove(null, 123));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullValue_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.remove("123", null));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullKey_andNullValue_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.remove(null, null));
    }
    @Test
    public void givenMap_onRemove_key_value_thatDoesNotExist_returns_false() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertFalse(map.remove("123", 456));
    }

    @Test
    public void givenMap_onRemove_key_value_thatDoNotMatch_returns_false() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        map.put("123", 456);
        assertFalse(map.remove("123", 789));
    }

    @Test
    public void givenMap_onRemove_key_value_thatDoMatch_returns_true() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        map.put("123", 456);
        assertTrue(map.remove("123", 456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onClear_returnsEmptyMap_andNullValueOnGettingKey_123() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);
        assertEquals(1, map.size());

        map.clear();
        value = map.get("123");
        assertNull(value);
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsKey_null_throws_NullPointerException() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertThrows(NullPointerException.class, ()-> map.containsKey(null));
    }

    @Test
    public void createMap_onKeySet_returnsAllKeysFromMap() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        for(int i = 0; i < 10; i++)
            map.put(i, i * 10);
        Set keysExpected = new HashSet<Integer>();
        for(int i = 0; i < 10; i++)
            keysExpected.add(i);
        Set keys = map.keySet();
        assertEquals(keysExpected, keys);
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueOfNull_throws_NullPointerException() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.getOrDefault(null, null));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesNotExist_returnsDefaultOf_1000() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        for (int i = 0; i < 13; i++) map.put(i, i * 10);
        assertEquals(1000, map.getOrDefault(100, 1000));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesExist_returnsValueOf_100() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        for (int i = 0; i < 13; i++) map.put(i, i * 10);
        assertEquals(100, map.getOrDefault(10, 1000));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsKey_123_returns_true() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertTrue(map.containsKey("123"));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsKey_780_returns_false() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertFalse(map.containsKey("789"));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_456_returns_true() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertTrue(map.containsValue(456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_789_returns_false() {
        CustomMap map = new CustomMap(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertFalse(map.containsValue(789));
    }

    @Test
    public void onAdding_13Items_withLoadFactorOf_point_75_returns_MapSizeOf_23_andCorrectlyRehashedValues() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        assertEquals(17, map.getMapSize());
        for (int i = 0; i < 13; i++) map.put(i, i * 10);
        assertEquals(23, map.getMapSize());
        for(int i = 0; i < 7; i++) assertEquals(i * 10, map.get(i));
    }

    @Test
    public void onAdding_13Items_withLoadFactorOf_point_75_returns_MapSizeOf_23_andOnReducingMapToSizeOf_5_reducesMapSizeTo_17() {
        CustomMap map = new CustomMap(Integer.class, Integer.class);
        assertEquals(17, map.getMapSize());
        for(int i = 1; i < 14; i++) map.put(i, i * 10);
        assertEquals(23, map.getMapSize());
        assertEquals(13, map.size());

        for(int i = 1; i < 9; i++) map.remove(i);

        assertEquals(5, map.size());
        assertEquals(17, map.getMapSize());

        for(int i = 10; i < 14; i++) assertEquals(i * 10, map.get(i));
    }
}