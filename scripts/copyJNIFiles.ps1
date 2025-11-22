$ErrorActionPreference = "Stop"

function Copy-File {
    param (
        [string]$Source,
        [string]$Destination
    )
    Write-Host "Copying $Source to $Destination"
    Copy-Item -Force -Path $Source -Destination $Destination
}

$jniDir = "Application/LinkBubble/src/main/jni"

Copy-File "node_modules/abp-filter-parser-cpp/ABPFilterParser.cpp" "$jniDir/ABPFilterParser.cpp"
Copy-File "node_modules/abp-filter-parser-cpp/ABPFilterParser.h" "$jniDir/ABPFilterParser.h"
Copy-File "node_modules/abp-filter-parser-cpp/cosmeticFilter.cpp" "$jniDir/cosmeticFilter.cpp"
Copy-File "node_modules/abp-filter-parser-cpp/cosmeticFilter.h" "$jniDir/cosmeticFilter.h"
Copy-File "node_modules/abp-filter-parser-cpp/filter.cpp" "$jniDir/filter.cpp"
Copy-File "node_modules/abp-filter-parser-cpp/filter.h" "$jniDir/filter.h"
Copy-File "node_modules/abp-filter-parser-cpp/base.h" "$jniDir/base.h"
Copy-File "node_modules/abp-filter-parser-cpp/badFingerprint.h" "$jniDir/badFingerprint.h"
Copy-File "node_modules/abp-filter-parser-cpp/badFingerprints.h" "$jniDir/badFingerprints.h"

Copy-File "node_modules/bloom-filter-cpp/BloomFilter.cpp" "$jniDir/BloomFilter.cpp"
Copy-File "node_modules/bloom-filter-cpp/BloomFilter.h" "$jniDir/BloomFilter.h"
Copy-File "node_modules/bloom-filter-cpp/hashFn.h" "$jniDir/hashFn.h"
Copy-File "node_modules/bloom-filter-cpp/hashFn.cpp" "$jniDir/hashFn.cpp"

Copy-File "node_modules/hashset-cpp/HashSet.cpp" "$jniDir/HashSet.cpp"
Copy-File "node_modules/hashset-cpp/HashSet.h" "$jniDir/HashSet.h"
Copy-File "node_modules/hashset-cpp/HashItem.h" "$jniDir/HashItem.h"

Copy-File "node_modules/tracking-protection/node_modules/hashset-cpp/hash_set.h" "$jniDir/hash_set.h"
Copy-File "node_modules/tracking-protection/node_modules/hashset-cpp/hash_item.h" "$jniDir/hash_item.h"

Copy-File "node_modules/tracking-protection/FirstPartyHost.cpp" "$jniDir/FirstPartyHost.cpp"
Copy-File "node_modules/tracking-protection/TrackerData.cpp" "$jniDir/TrackerData.cpp"
Copy-File "node_modules/tracking-protection/FirstPartyHost.h" "$jniDir/FirstPartyHost.h"
Copy-File "node_modules/tracking-protection/TrackerData.h" "$jniDir/TrackerData.h"
Copy-File "node_modules/tracking-protection/TPParser.h" "$jniDir/TPParser.h"
Copy-File "node_modules/tracking-protection/TPParser.cpp" "$jniDir/TPParser.cpp"

Write-Host "Patching files..."
(Get-Content "$jniDir/FirstPartyHost.cpp") -replace 'test/hashFn.h', 'hashFn.h' | Set-Content "$jniDir/FirstPartyHost.cpp"
(Get-Content "$jniDir/TrackerData.cpp") -replace 'test/hashFn.h', 'hashFn.h' | Set-Content "$jniDir/TrackerData.cpp"

Write-Host "Done."
