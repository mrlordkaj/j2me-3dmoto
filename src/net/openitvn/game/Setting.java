/*
 * Copyright (C) 2012 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.openitvn.game;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Thinh Pham
 */
public class Setting {
    private RecordStore rs;
    private final String name;
    
    public Setting(String fileName, String[] defaultRecord) {
        name = fileName;
        
        try {
            rs = RecordStore.openRecordStore(name, true);
//            //if the structure of records are not match with default,
//            //delete it and create default one.
//            if(rs.getNumRecords() != defaultRecord.length) {
//                rs.closeRecordStore();
//                RecordStore.deleteRecordStore(name);
//                rs = RecordStore.openRecordStore(name, true);
//                for(int i = 0; i < defaultRecord.length; i++) {
//                    byte[] writer = defaultRecord[i].getBytes();
//                    rs.addRecord(writer, 0, writer.length);
//                }
//            }
            int numRecord = rs.getNumRecords();
            if(numRecord < defaultRecord.length) {
                for(int i = numRecord; i < defaultRecord.length; i++) {
                    byte[] writer = defaultRecord[i].getBytes();
                    rs.addRecord(writer, 0, writer.length);
                }
            }
        } catch (RecordStoreException ex) { }
    }
    
    public boolean storeSetting(int key, String value) {
        byte[] writer = value.getBytes();
        try {
            rs.setRecord(key+1, writer, 0, writer.length);
            return true;
        }
        catch (InvalidRecordIDException ex) { 
        }
        catch (RecordStoreException ex) {
        }
        return false;
    }
    
    public String getSetting(int key) {
        try {
            return new String(rs.getRecord(key+1));
        } catch (InvalidRecordIDException ex) {
            return null;
        } catch (RecordStoreException ex) {
            return null;
        }
    }
    
    public boolean saveSetting() {
        try {
            rs.closeRecordStore();
            rs = RecordStore.openRecordStore(name, false);
            return true;
        } catch (RecordStoreException ex) {
            return false;
        }
    }
    
    public void dispose() {
        try {
            rs.closeRecordStore();
        } catch (RecordStoreException ex) { }
    }
}
