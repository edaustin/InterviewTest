
Notes

Time taken 2.5 hrs
Environment: Android Studio 2.2

Sorted minor build issues (Support library/ListView). Modified build.gradle for app, build tools and other stuff.

Added Internet Manifest permissions.

Moved both item and image off main thread and passed handlers for callback processed by idiomatic Android concurrency constructs.

First callback straightforward, second via anon inner class to receive callback – may be GC issues.

Messy checked exceptions still remain.

Added Cache as per Android Developer site recommendations.

XML layout changes for the listview

Layout briefly tested on a few devices

Rotation already works (seemingly) via current savedInstanceState on the fragment in the standard activity lifecycle.

Didn’t have time to optimize for GC and effiiciency or profile the code, not run through Lint, expect issues there.

Needs a huge going over.

Testing.
None, deployed to a GenyMotion Nexus5 6.0.0 instance and a Generic 4.4.4 device... works, rotates.

Third party libraries?
Apache Commons httpclient
too simple to need anything really...

TO DO:
rewrite completely.
Cache images.
Lazy loading.


