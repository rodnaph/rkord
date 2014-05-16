# vim filetype=ruby

Vagrant.configure("2") do |config|

  config.vm.box = "centos65"
  config.vm.network :private_network, ip: '192.168.33.99'

  config.vm.provision "ansible" do |ansible|
    ansible.playbook = "ansible/rkord.yml"
    ansible.inventory_path = "ansible/vm.ini"
    ansible.host_key_checking = false
    ansible.limit = 'all'
  end

end

