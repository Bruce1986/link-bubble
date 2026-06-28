# Node Dependency Findings

Date: 2026-03-15

## Summary

`npm install` is currently not reliable on this repo with modern local toolchains.

Tested on this machine:

- Node `v18.20.8`: failed
- Node `v20.20.1`: failed
- Node `v22.16.0`: failed

All three versions failed in the same dependency path:

- `tracking-protection`
- nested dependency `hashset-cpp`
- `node-gyp rebuild`

The current `AGENTS.md` guidance still says to use Node 18, but Node 18 alone is no longer sufficient on this machine.

## What These npm Dependencies Are Used For

The npm dependencies are not only for linting.

[package.json](../package.json) defines:

- `postinstall`: `sh ./scripts/copyJNIFiles.js`
- runtime/build dependencies:
  - `abp-filter-parser-cpp`
  - `tracking-protection`

[scripts/copyJNIFiles.js](../scripts/copyJNIFiles.js) copies C++ source/header files from `node_modules` into `Application/LinkBubble/src/main/jni/`.

This means the npm dependency chain is part of the Android native/JNI build preparation flow, not just local JS tooling.

## Historical Notes

Relevant history found during investigation:

- `776f7b60` on 2015-12-17: added `scripts/copyJNIFiles.js`
- `2ab7f287` on 2025-06-12: clarified Node usage in `AGENTS.md`
- `31693098` on 2025-06-29: clarified Node 18 requirement in `AGENTS.md`
- `2afe9f2d` on 2025-06-12: added `node-gyp` overrides in `package.json`

This suggests the npm/native flow is old, but it is still considered active enough that maintainers tried to keep it working in 2025.

## Observed Failure Pattern

The install failure is not limited to Node 20 or Node 22.

Node 18, 20, and 22 all failed while building old native addon code from `hashset-cpp`.

Representative symptoms from the logs:

- failure during `node-gyp rebuild`
- C++ compile errors against Node headers
- old addon code appearing incompatible with current Node/V8 header expectations

Based on the errors, the likely problem is the age of the native addon dependency chain rather than only the selected Node major version.

## Current Conclusion

- We should not treat Node 20 or Node 22 as safe replacements today.
- We also should not assume Node 18 is still enough in a modern macOS environment.
- The real maintenance issue is the old npm/native dependency chain itself.

## Possible Next Steps

1. Vendor the required JNI C++ sources directly into the repo and remove the `postinstall` dependency on npm native addons.
2. If npm must stay in the loop, fork or replace `tracking-protection` and `hashset-cpp`.
3. Update `AGENTS.md` after the build path is actually validated again, instead of only changing the stated Node version.
