import cn.imaq.cerdns.util.CIDR;
import cn.imaq.cerdns.util.Config;
import org.junit.Test;

public class CIDRMatchTest {
    private String[] ips = {"66.151.25.22","223.252.207.21","211.151.238.88","199.15.215.121","165.227.114.131","165.227.67.158","138.197.13.10","45.55.37.194","151.139.106.84","140.207.135.22","140.207.128.31","140.207.127.54","140.205.218.52","140.205.218.51","106.11.250.136","106.11.250.73","106.11.250.12","106.11.249.212","106.11.249.7","203.190.124.28","203.190.124.26","203.190.124.25","203.190.124.18","203.190.124.16","203.190.124.14","203.190.124.12","203.190.124.11","118.31.229.59","17.178.96.59","17.172.224.47","17.142.160.59","52.175.23.79","52.229.175.79","52.229.170.224","52.229.174.172","52.229.174.233","162.247.242.21","162.247.242.20","162.247.242.19","162.247.242.18","210.28.129.4","106.75.75.228","207.159.144.101","182.161.72.73","1.1.1.1","52.239.151.138","213.186.33.16","40.77.228.74","119.167.195.224","119.167.195.221","1.27.242.101","1.27.242.100","1.27.242.99","1.27.242.98","1.27.242.97","1.27.242.96","1.27.242.95","1.27.242.94","8.26.198.253","104.19.196.102","104.19.195.102","104.19.194.102","104.19.193.102","104.19.192.102","223.167.154.116","223.167.154.115","223.166.150.143","223.166.150.142","91.250.96.112","216.58.208.206","90.216.144.193","123.125.102.190","111.206.200.135","202.120.38.175","67.225.216.6","104.20.151.16","104.20.150.16","117.18.237.29","152.195.61.98","150.229.76.170","150.229.12.170","54.230.159.193","54.230.159.118","54.230.159.107","54.230.159.84","54.230.159.73","54.230.159.37","54.230.159.13","54.230.159.5","203.208.51.60","203.208.51.59","202.199.27.11","163.177.92.56","203.208.48.46","203.208.48.41","203.208.48.40","203.208.48.39","203.208.48.38","203.208.48.37","203.208.48.36","203.208.48.35","203.208.48.34","203.208.48.33","203.208.48.32","202.119.32.6","172.217.160.14","172.217.160.14","13.107.5.88","111.202.114.81","46.236.37.16","46.236.37.8","46.236.37.4","172.217.160.14","66.151.25.19","173.241.248.220","223.167.86.106","140.207.128.115","140.206.160.163","78.47.188.145","123.125.125.86","123.125.125.85","61.135.189.216","61.135.189.215","172.217.24.74","74.125.130.95","74.125.68.95","74.125.24.95","202.118.1.130","90.216.130.160","104.131.83.215","45.248.87.133","111.221.29.253","40.77.226.16","111.221.29.74","111.221.29.75","111.221.29.76","111.221.29.77","111.221.29.79","111.221.29.81","111.221.29.81","111.221.29.82","111.221.29.84","111.221.29.84","111.221.29.85","111.221.29.87","111.221.29.88","111.221.29.92","111.221.29.96","111.221.29.97","111.221.29.98","111.221.29.100","111.221.29.102","111.221.29.102","111.221.29.103","111.221.29.105","111.221.29.107","111.221.29.107","111.221.29.108","111.221.29.109","111.221.29.109","111.221.29.110","111.221.29.114","111.221.29.116","111.221.29.117","111.221.29.117","111.221.29.118","111.221.29.118","111.221.29.119","111.221.29.121","111.221.29.121","111.221.29.122","111.221.29.123","111.221.29.124","111.221.29.126","111.221.29.126","111.221.29.130","111.221.29.134","111.221.29.135","111.221.29.136","111.221.29.138","111.221.29.139","111.221.29.140","111.221.29.141","111.221.29.142","111.221.29.145","111.221.29.146","111.221.29.147","111.221.29.149","111.221.29.150","111.221.29.151","111.221.29.152","111.221.29.153","111.221.29.153","111.221.29.156","111.221.29.157","111.221.29.160","111.221.29.162","111.221.29.163","111.221.29.163","111.221.29.164","111.221.29.169","111.221.29.169","111.221.29.170","111.221.29.171","111.221.29.172","111.221.29.66","111.221.29.198","223.252.199.7","223.252.199.5","185.113.25.60","185.113.25.59","123.129.215.252","123.129.215.221","119.167.195.235","119.167.195.234","119.167.195.224","119.167.195.223","119.167.195.222","119.167.195.220","119.167.195.219","36.250.235.88","36.250.235.87","36.250.235.85","36.250.235.83","27.221.59.224","27.221.59.223","27.221.34.144","123.129.215.252","123.129.215.179","119.167.195.235","119.167.195.234","119.167.195.224","119.167.195.223","119.167.195.221","119.167.195.220","119.167.195.219","36.250.235.101","36.250.235.100","36.250.235.86","36.250.235.83","27.221.59.222","27.221.59.220","27.221.34.142","218.94.136.185","46.30.213.247","46.30.213.30","159.122.93.22","166.111.5.99","192.0.0.171","192.0.0.170","202.118.76.254","34.252.53.170","173.241.240.143","192.30.253.124","162.13.143.216","63.251.252.12","45.248.87.133","159.148.147.196","159.122.93.22","159.122.93.30","117.27.224.152","117.27.224.151","202.119.32.12","210.28.129.9","17.154.67.28","216.239.32.62","216.239.32.64","203.208.43.122","203.208.43.121","203.208.43.109","74.125.24.157","74.125.24.156","74.125.24.155","74.125.24.154","210.226.40.63","66.235.139.207","66.235.139.206","66.235.139.205","66.235.139.19","66.235.139.18","66.235.139.17","66.235.138.195","66.235.138.194","66.235.138.193","63.140.44.224","63.140.44.112","199.15.215.120","203.208.43.122","203.208.43.121","203.208.43.109","210.176.156.41","210.176.156.31","210.176.156.21","210.176.156.81","210.176.156.71","210.176.156.61","140.143.182.230","58.250.137.86","210.176.156.45","210.176.156.35","210.176.156.25","52.208.50.140","172.217.160.14","144.2.1.1","59.111.0.129","1.1.1.1","173.194.4.198","173.194.51.247","74.125.5.16","173.194.56.241","173.194.56.50","203.208.42.35","173.194.4.167","74.125.10.89","74.125.102.74","173.194.59.106","74.125.5.9","173.194.152.138","74.125.10.91","173.194.152.75","203.208.42.24","74.125.102.108","58.162.62.82","113.142.9.51","123.125.102.48","111.206.200.71","140.205.250.3","13.107.3.128","5.35.253.150","134.119.68.3","66.115.184.84","118.178.117.96","219.219.113.225","203.208.51.95","203.208.51.88","203.208.51.87","203.208.51.79","8.254.249.76","221.199.209.2","202.118.1.66","123.125.114.101","129.78.5.8","220.249.243.39","163.177.83.213","112.90.77.199","66.151.25.21","140.207.189.124","140.207.128.111","58.247.206.164","140.207.135.38","140.207.62.107","104.244.42.129","104.244.42.65","173.241.248.143","183.47.232.16","183.47.232.15","183.47.232.14","183.47.232.13","121.12.98.167","121.12.98.166","121.12.98.165","121.12.98.138","121.12.98.136","121.12.98.135","106.2.69.93","173.241.248.143","221.5.35.70","210.28.141.141","17.253.53.203","17.253.53.203","103.67.200.188","103.67.200.187","159.148.147.205","112.80.248.74","112.80.248.73","172.217.160.4","74.125.200.94","211.101.228.185","104.155.104.29","159.148.147.196","202.58.60.194","172.217.160.14","118.178.155.85","118.178.115.245","101.69.185.254","101.69.185.240","101.69.185.239","101.69.185.238","101.69.185.209","101.69.185.208"};

    @Test
    public void test() throws Exception {
        Config.loadConfig("config.json");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            for (String ip : ips) {
                System.out.print(ip + ": ");
                for (Config.ChainNode node : Config.getChain()) {
                    if (node.getMatchPrefixes() != null) {
                        int ipInt = CIDR.toInt(ip);
                        for (byte j = 0; j < 32; j++) {
                            if (node.getMatchPrefixes().contains(new CIDR.Prefix(ipInt >> (32 - j), j))) {
                                System.out.print(node);
                                break;
                            }
                        }
//                        for (CIDR.Prefix prefix : node.getMatchPrefixes()) {
//                            if (CIDR.match(ip, prefix)) {
//                                System.out.print(node);
//                                break;
//                            }
//                        }
                    }
                }
                System.out.println();
            }
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }
}