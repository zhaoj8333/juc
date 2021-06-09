package thrd.pool.locality;

import fr.ujm.tse.lt2c.satin.cache.size.CacheInfo;
import fr.ujm.tse.lt2c.satin.cache.size.CacheLevel;
import fr.ujm.tse.lt2c.satin.cache.size.CacheLevelInfo;
import fr.ujm.tse.lt2c.satin.cache.size.CacheType;

public class CpuCacheInfo {
    public static void main(String[] args) {
        CacheInfo cache = CacheInfo.getInstance();
        CacheLevelInfo l1DataCache = cache.getCacheInformation(CacheLevel.L1, CacheType.DATA_CACHE);
        System.out.println(l1DataCache);


        CacheLevelInfo l1InstructionCache = cache.getCacheInformation(CacheLevel.L1, CacheType.INSTRUCTION_CACHE);
        System.out.println(l1InstructionCache);
    }
}
