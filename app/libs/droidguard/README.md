a module written in Go for cryptography service and Android untrusted environment detection

Requirement: [gomobile](https://pkg.go.dev/golang.org/x/mobile/cmd/gomobile) (developed by the Go team)

#### Build command
```gomobile bind -target=android -o build/droidguard.aar -ldflags="-s -w -buildid=0" -trimpath ./src```

simply import to android studio as how you would use a jar/aar library. testing available in `androidTest`

#### Security concerns
Despite being compiled to native code and all the stripping options, the Go language keep descriptive information such as function/package names, code filename and code line numbers in the binary. This is just how Go normally works under the hood.

Further obfuscation/stripping requires 3rd party involvement that modifies the build pipeline.