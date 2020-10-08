onos-netcfg localhost ~/Dlink-onos-2.2.2/onos-dhcp.json
onos localhost app activate dhcp proxyarp
onos-app localhost install! ~/Dlink-onos-2.2.2/authentication/target/authentication-1.0-SNAPSHOT.oar
onos-app localhost install! ~/Dlink-onos-2.2.2/captiveportal/target/captiveportal-1.0-SNAPSHOT.oar
onos localhost apps -a -s
ping -c 5 192.168.44.198
