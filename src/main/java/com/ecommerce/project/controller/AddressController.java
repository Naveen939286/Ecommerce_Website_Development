package com.ecommerce.project.controller;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController
{
    @Autowired
    AuthUtil authUtil;

    @Autowired
    AddressService addressService;

    @PostMapping("/addresses")
    //Here we are creating the Address
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO)
    {
        //We have to pass logged in user to get which user logged in so we create instance of user.
        //In authutil we have all the helper methods
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.OK);
    }

    @GetMapping("/addresses")
    //get all the addresses
    public ResponseEntity<List<AddressDTO>> getAddresses()
    {
        List<AddressDTO> addressList = addressService.getAddresses();
        return new ResponseEntity<>(addressList, HttpStatus.CREATED);
    }

    @GetMapping("/addresses/{addressId}")
    //Getting a specific address with the address id
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId)
    {
        //get a single address and store in the AddressDTO object
        AddressDTO addressDTO = addressService.getAddressesById(addressId);
        return  new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    //getting address by the user
    //this accept the logged in user object and get the address
    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses()
    {
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressList = addressService.getUserAddresses(user);
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    //Updating the Address
    @PutMapping("/addresses/{addressId}")
    //need one address at a  time to update so dont need list
    //here we are updating the address so need address DTO
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId,
                                                        @Valid @RequestBody AddressDTO addressDTO)
    {
        AddressDTO updatedAddress = addressService.updateAddress(addressId,addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    //Delete the Address
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId)
    {
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
