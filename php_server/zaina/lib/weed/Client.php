<?php
namespace WeedPhp;

use WeedPhp\Transport\Curl;

/**
 *
 * Client for Weed-fs.
 * 
 * http://code.google.com/p/weed-fs/
 *
 * @author micjohnson
 * 
 * TODO look at /admin/assign_volume, /admin/assign_volume_check, /admin/assign_volume_commit, /admin/assign_volume_compact
 * 
 */
class Client
{
    /** @var Curl transport */
    protected $transport;

    /** @var string weed-fs master server http://ip:port */
    protected $storageAddress;

    /**
     * 
     * Create curl transport and set master server address
     * 
     * @param string $storageAddress
     */
    public function __construct($storageAddress)
    {
        $this->transport = new Curl();
        $this->storageAddress = $storageAddress;
    }

    /**
     *
     * Get a fid and a volume server url
     *
     * for replication options see:
     * http://code.google.com/p/weed-fs/#Rack-Aware_and_Data_Center-Aware_Replication
     * 
     * @param number $count
     * @param string $replication
     * @return mixed response from curl
     */
    public function assign($count = 1, $replication = null)
    {
        $assignUrl = $this->storageAddress . '/dir/assign';
        $assignUrl .= '?count=' . intval($count);
        
        if($replication !== null) {
            $assignUrl .= '&replication=' . $replication;
        }
        
        $response = $assignUrl = $this->transport->get($assignUrl);
        
        $this->transport->close();

        // {"count":1,"fid":"3,01637037d6","url":"127.0.0.1:8080","publicUrl":"localhost:8080"}
        return $response;
    }

    /**
     *
     * Delete a file by fid on specified volume server
     *
     * @param string $storageVolumeAddress
     * @param string $fid file id
     * @return mixed
     */
    public function delete($volumeServerAddress, $fid)
    {
        $deleteUrl = $volumeServerAddress . '/' . $fid;
        // TODO check for http://
        
        $response = $this->transport->custom($deleteUrl, 'DELETE');
        
        $this->transport->close();

        return $response;
    }

    /**
     *
     * Lookup locations for specified volume by id
     *
     * @param number $volumeId
     * @return mixed
     */
    public function lookup($volumeId)
    {
        $lookupUrl = $this->storageAddress . '/dir/lookup';
        $lookupUrl .= '?volumeId=' . $volumeId;
        
        $response = $this->transport->get($lookupUrl);
        
        $this->transport->close();

        // {"locations":[{"publicUrl":"localhost:8080","url":"localhost:8080"}]}
        return $response;
    }
    
    /**
     *
     * This will assign $count volumes with $replication replication.
     *
     * for replication options see:
     * http://code.google.com/p/weed-fs/#Rack-Aware_and_Data_Center-Aware_Replication
     *
     * @param number $count number of volumes
     * @param string $replication something like 001
     */
    public function grow($count, $replication)
    {
        $growUrl = $this->storageAddress . '/vol/grow';
        $growUrl .= '?count=' . $count;
        $growUrl .= '&replication=' . $replication;
        
        $response = $this->transport->get($growUrl);
        
        $this->transport->close();

        return $response;
    }
    
    /**
     *
     * Retrieve a file from a specific volume server by fid
     *
     * @param string $volumeServerAddress
     * @param string $fid
     * @return mixed
     */
    public function retrieve($volumeServerAddress, $fid)
    {
        $retrieveUrl = $volumeServerAddress . '/' . $fid;
        // TODO check for http://
        
        $response = $this->transport->get($retrieveUrl);
        
        $this->transport->close();

        return $response;
    }

    /**
     *
     * Get information about volume's free space
     *
     */
    public function status()
    {
        $statusAddress = $this->storageAddress . '/dir/status';
        
        $response = $this->transport->get($statusAddress);
        
        $this->transport->close();

        return $response;
    }
    
    /**
     * 
     * Is in source, haven't tested
     * 
     * @return mixed
     */
    public function volumeStatus()
    {
        $statusAddress = $this->storageAddress . '/vol/status';
        
        $response = $this->transport->get($statusAddress);
        
        $this->transport->close();

        return $response;
    }
    
    /**
     * 
     * Is in source, need to test
     * 
     * @param string $volumeServerAddress
     */
    public function volumeServerStatus($volumeServerAddress)
    {
    	$statusAddress = $volumeServerAddress . '/status';
    	
    	$response = $this->transport->get($statusAddress);
    	
    	$this->transport->close();
    	
    	return $response;
    }

    /**
     * 
     * Store multiple files at once, assuming you have assigned the same number of count for fid
     * as you have number of files.
     * 
     * @param string $volumeServerAddress
     * @param string $fid base fid for all files
     * @param array $files
     * @return mixede
     */
    public function storeMultiple($volumeServerAddress, $fid, array $files)
    {
        $count = count($files);
        
        $storeUrl = $volumeServerAddress . '/' . $fid;
        // TODO check for http://

        $response = array();
        for($i = 1; $i <= $count; $i++) {
            $parameters = array('file'=>$files[$i-1]);
            
            $response[] = $this->transport->post($storeUrl, $parameters);
            
            $storeUrl = $volumeServerAddress . '/' . $fid . '_' . $i;
        }
        
        $this->transport->close();

        return $response;
    }

    /**
     * 
     * Store a single file on volume server. Use assign first to get the volume server
     * and fid
     * 
     * @param string $volumeServerAddress
     * @param string $fid
     * @param unknown $file
     * @return mixed
     */
    public function store($volumeServerAddress, $fid, $file)
    {
        $storeUrl = $volumeServerAddress . '/' . $fid;
        
        $parameters = array('file'=>$file);
        
        $response = $this->transport->post($storeUrl, $parameters);
        
        $this->transport->close();

        return $response;
    }
}