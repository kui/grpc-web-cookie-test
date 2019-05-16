grpc-web での Cookie の扱い検証
===================================

tl;dr HTTP ヘッダはフィールド名が小文字に変換されて gRPC のメタデータとして渡される。

サーバ側で grpc のメタデータすべてダンプする処理を使って、grpc-web での HTTP ヘッダや Cookie の扱いがどうなっているか検証した

Cookie の送信
----------------

通常の xhr による POST リクエストになるので Cookie は送出されていた。

以下ブラウザの開発者ツールからのコピペ:

```
Accept: application/grpc-web-text
Accept-Encoding: gzip, deflate, br
Accept-Language: ja,en-US;q=0.9,en;q=0.8
Connection: keep-alive
Content-Length: 8
Content-Type: application/grpc-web-text
Cookie: foo=bar; hoge=fuga
Host: localhost:8080
Origin: http://localhost:8080
Referer: http://localhost:8080/
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36
X-Grpc-Web: 1
X-User-Agent: grpc-web-javascript/0.1
```


Cookie の受信
----------------

ヘッダーフィールド名が小文字に変換されていたがそれ以外はすべてのヘッダが envoy によってアプリケーションに渡されていた。

アプリケーションログからのコピペ:

```
api_1    | 2019-05-16 11:33:33.526  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : 13 header entries
api_1    | 2019-05-16 11:33:33.530  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : x-request-id: [34cfc86b-03c4-47fc-9f82-fd8a418e2ab8]
api_1    | 2019-05-16 11:33:33.530  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : referer: [http://localhost:8080/]
api_1    | 2019-05-16 11:33:33.530  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : grpc-accept-encoding: [identity,deflate,gzip]
api_1    | 2019-05-16 11:33:33.530  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : accept-language: [ja,en-US;q=0.9,en;q=0.8]
api_1    | 2019-05-16 11:33:33.530  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : cookie: [foo=bar; hoge=fuga]
api_1    | 2019-05-16 11:33:33.531  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : x-forwarded-proto: [http]
api_1    | 2019-05-16 11:33:33.531  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : x-grpc-web: [1]
api_1    | 2019-05-16 11:33:33.531  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : origin: [http://localhost:8080]
api_1    | 2019-05-16 11:33:33.531  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : x-user-agent: [grpc-web-javascript/0.1]
api_1    | 2019-05-16 11:33:33.532  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : accept: [application/grpc-web-text]
api_1    | 2019-05-16 11:33:33.532  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : content-type: [application/grpc]
api_1    | 2019-05-16 11:33:33.532  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : accept-encoding: [gzip, deflate, br]
api_1    | 2019-05-16 11:33:33.532  INFO 1 --- [ault-executor-0] kui.test.DumpMetadataInterceptor         : user-agent: [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36]
```

簡単にしか書かれていないが、公式のドキュメント通りではあった。

> https://github.com/grpc/grpc/blob/master/doc/PROTOCOL-WEB.md
> 
> Key-value pairs encoded as a HTTP/1 headers block (without the terminating newline), per https://tools.ietf.org/html/rfc7230#section-3.2

もうちょっと厳密に書いてほしかった。。。key-value とは。


追証
------------------

JDK 8 以上と node 10 以上を想定している

```
cd web
npm install
npm run build
cd ../api
./gradlew build
cd ..
docker-compose up --build
```

http://localhost:8080 にアクセス

ブラウザコンソールから JS で Cookie の設定

例:

```
document.cookie = 'hoge=fuga;path=/;'
```

"greet" ボタンを押す

開発者ツールでリクエストを確認（失敗する）

docker-compose の端末で上記のようにメタデータすべてがログ出力される
