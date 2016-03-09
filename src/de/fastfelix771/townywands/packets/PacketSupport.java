package de.fastfelix771.townywands.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import de.fastfelix771.townywands.utils.Reflect.Version;

@RequiredArgsConstructor
public enum PacketSupport {

    NONE(new ArrayList<Version>(0)),
    
    BOTH(Arrays.asList(         Version.v1_9, Version.v1_8, Version.v1_7  )),
    
    NMS(Arrays.asList(          Version.v1_9, Version.v1_8, Version.v1_7  )), 
    
    ProtocolLib(Arrays.asList(  Version.v1_9, Version.v1_8, Version.v1_7  ));
    
    @Getter private final List<Version> supportedVersions;
    
    /**
     * Check if there are PacketHandlers available for the given Version.
     * It will always prefer ProtocolLib.
     */
    public static PacketSupport forVersion(Version version) {
        
        if(BOTH.getSupportedVersions().contains(version)) return BOTH;
        
        if(ProtocolLib.getSupportedVersions().contains(version)) return ProtocolLib;
        
        if(NMS.getSupportedVersions().contains(version)) return NMS;
        
        return NONE;
    }
    
}