package com.moseph.mra;

/* This is file is generated by the ontology bean generator.  
DO NOT EDIT, UNLESS YOU ARE REALLY REALLY SURE WHAT YOU ARE DOING! */

/** file: SlotHolder
 * @author ontology bean generator (Acklin BV) 
 * @version 2006/01/26, 14:34:55
 */

public class SlotHolder {

  public boolean equals(Object o) {
    if (o instanceof SlotHolder) {
      SlotHolder other = (SlotHolder) o;
      if (other.className.equalsIgnoreCase(className) && other.slotName.equalsIgnoreCase(slotName)) {
        return true;
      }
    }
    return false;
  }

  public int hashCode() {
    int retValue;

    retValue = new String(className + "_" + slotName).toLowerCase().hashCode();
    return retValue;
  }

  public SlotHolder(String className, String slotName) {
    this.className = className;
    this.slotName = slotName;
  }
  public String className;
  public String slotName;
}
