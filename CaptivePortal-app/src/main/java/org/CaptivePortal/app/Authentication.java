package org.CaptivePortal.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onlab.packet.IPv4;

import org.json.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

public class Authentication {

	private final Logger log = LoggerFactory.getLogger(getClass());

	// private String portalMac = "ea:e9:78:fb:fd:2d";
	private String portalMac = "f6:42:0f:83:51:de";

	// private String gatewayMac = "00:50:56:fc:6e:36";
	private String gatewayMac = "ea:e9:78:fb:fd:00";

	private String src_mac;
	private String dst_mac;
	private String src_ip;
	private String dst_ip;
	private String src_port = "";
	private String dst_port = "";
	private byte protocol;

	private String src_access_sw;
	private String src_access_port;
	private String dst_access_sw;
	private String dst_access_port;

	private String in_sw;
	private String in_port;

	private String time;
	// Add src_group
	private String src_group = "";

	private String src_user = "";
	private String dst_user = "";

	private boolean mac_enable = false;

	private String result = "Drop";

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Authentication(String src_mac, String dst_mac, String src_ip, String dst_ip, String src_port,
			String dst_port, byte protocol, String src_access_sw, String src_access_port, String dst_access_sw,
			String dst_access_port, String in_sw, String in_port, String time) {

		this.src_mac = src_mac;
		this.dst_mac = dst_mac;
		this.src_ip = src_ip;
		this.dst_ip = dst_ip;
		this.protocol = protocol;

		if (protocol == IPv4.PROTOCOL_TCP || protocol == IPv4.PROTOCOL_UDP) {
			this.src_port = src_port;
			this.dst_port = dst_port;
		}

		this.src_access_sw = src_access_sw;
		this.src_access_port = src_access_port;
		this.dst_access_sw = dst_access_sw;
		this.dst_access_port = dst_access_port;

		this.in_sw = in_sw;
		this.in_port = in_port;

		this.time = time;

		Process process;
		try {
			String line = "curl -X POST http://localhost:8181/RadiusAuthentication/UserCredential/insertIpMapping -u onos:rocks -d ip="
			+ src_ip +"&mac="+src_mac;
			process = Runtime.getRuntime().exec(line);
			int returnVal=0;
			try{
				returnVal = process.waitFor();
			}catch(InterruptedException e){
				log.info("interrupted exception for exec curl");
			};
			// log.info(Integer.toString(returnVal));
			line = "";
			BufferedReader p_in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			line = p_in.readLine();
			// log.info("line in Auth. constructor : "+line);
			if (line == null) {
				log.info("line is null in Auth. constructor!!");
			}
		} catch (IOException e) {
			log.info("in Authentication getUser IOException");
		}
		;
	}

	public String accessCheck() {
		/**
		 * Check whether the packet can pass or it needs redirection
		 *
		 * @return A string of Pass/Drop/PktFromPortal/RedirectToPortal Pass: Host
		 *         transmitting the packet is authenticated Drop: The packet cannot be
		 *         transmitted in this network PktFromPortal: Packets for authentication
		 *         from portal RedirectToPortal: Redirect the packet to portal for
		 *         authentication
		 **/

		// Pass DHCP packets
		if (protocol == IPv4.PROTOCOL_UDP)
			if ((src_port.equals("67") && dst_port.equals("68")) || (src_port.equals("68") && dst_port.equals("67")))
				return "Pass";

		// Pass DNS packets
		if (protocol == IPv4.PROTOCOL_UDP || protocol == IPv4.PROTOCOL_TCP)
			if ((src_port.equals("53") || dst_port.equals("53")))
				return "Pass";


		if (protocol == IPv4.PROTOCOL_ICMP) {
			// Pass ICMP packets
			return "Pass";
		} else if (src_mac.equalsIgnoreCase(portalMac)) {
			// Packets from port 80/443/3000 of portal need some modification
			// Pass packets from other ports of portal
			if (src_port.equals("80") || src_port.equals("443") || src_port.equals("5001"))
				return "PktFromPortal";
			else
				return "Pass";
		}

		// Check whether the host is authenticated or not
		boolean src_enable = false;
		Process process;
		try {
			log.info(src_ip);
			String line = "curl -X POST http://localhost:8181/RadiusAuthentication/UserCredential/getUser -u onos:rocks -d ip="
			+ src_ip ;
			// log.info(line);
			process = Runtime.getRuntime().exec(line);
			int returnVal=0;
			try{
				returnVal = process.waitFor();
			}catch(InterruptedException e){
				log.info("interrupted exception for exec curl");
			};
			// log.info(Integer.toString(returnVal));
			line = "";
			BufferedReader p_in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			line = p_in.readLine();
			// log.info("line : "+line);
			if (line != null) {
				if (line.equals("true"))
				{
					log.info("getUser success!!");
					src_enable = true;
				}
				else
				{
					log.info("getUser failed!!");
					src_enable = false;
				}
			}
		} catch (IOException e) {
			log.info("in Authentication getUser IOException");
		}
		;

		if (protocol == IPv4.PROTOCOL_TCP) {
			if (dst_mac.equalsIgnoreCase(portalMac)) {
				// Pass any packets that its destination is portal
				return "Pass";
			} else if (src_mac.equalsIgnoreCase(gatewayMac)) {
				// Pass packets from gateway
				result = "Pass";
			} else if (!src_mac.equalsIgnoreCase(gatewayMac) && src_enable) {
				log.info("user authenticated!!!!!!!!!!!!!!!!!!!! & going to internet");
				result = "Pass";
				// updateExpirationTime();
			} else if (!src_mac.equalsIgnoreCase(portalMac) && !dst_mac.equalsIgnoreCase(portalMac)) {
				// If the packet is from unauthenticated host and destination is not portal,
				// redirect it to portal and update IP_MAC table
				if (dst_port.equals("80") || dst_port.equals("443") || dst_port.equals("5001")) {
					return "RedirectToPortal";
				}
			}
		}
		return result;
	}

}