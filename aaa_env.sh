onos-netcfg localhost ~/Dlink-onos-2.2.2/onos-dhcp.json
onos-netcfg localhost ~/aaa/app/aaa-conf.json
onos localhost app activate dhcp proxyarp
onos-app localhost install! ~/sadis/app/target/sadis-app-5.2.0-SNAPSHOT.oar
sleep 3
onos-app localhost install! ~/aaa/app/target/aaa-app-2.2.0-SNAPSHOT.oar
sleep 3
onos-app localhost install! ~/AAAfwd/target/aaafwd-1.0-SNAPSHOT.oar

