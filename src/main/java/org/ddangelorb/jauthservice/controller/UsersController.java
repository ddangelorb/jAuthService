package org.ddangelorb.jauthservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.ddangelorb.jauthservice.dto.UsersRequestDTO;
import org.ddangelorb.jauthservice.dto.UsersResponseDTO;
import org.ddangelorb.jauthservice.model.Users;
import org.ddangelorb.jauthservice.service.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/users")
@Api(tags = "users")
public class UsersController {
	  @Autowired
	  private UsersService userService;

	  @Autowired
	  private ModelMapper modelMapper;

	  @PostMapping("/signin")
	  @ApiOperation(value = "${UserController.signin}")
	  @ApiResponses(value = {//
	      @ApiResponse(code = 400, message = "Something went wrong"), //
	      @ApiResponse(code = 422, message = "Invalid username/password supplied")})
	  public String login(//
	      @ApiParam("Username") @RequestParam String username, //
	      @ApiParam("Password") @RequestParam String password) {
	    return userService.signin(username, password);
	  }

	  @PostMapping("/signup")
	  @ApiOperation(value = "${UserController.signup}")
	  @ApiResponses(value = {//
	      @ApiResponse(code = 400, message = "Something went wrong"), //
	      @ApiResponse(code = 403, message = "Access denied"), //
	      @ApiResponse(code = 422, message = "Username is already in use")})
	  public String signup(@ApiParam("Signup User") @RequestBody UsersRequestDTO user) {
	    return userService.signup(modelMapper.map(user, Users.class));
	  }

	  @PostMapping("/activate")
	  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
	  @ApiOperation(value = "${UserController.activate}", authorizations = { @Authorization(value="apiKey") })
	  @ApiResponses(value = {//
	      @ApiResponse(code = 400, message = "Something went wrong"), //
	      @ApiResponse(code = 403, message = "Access denied"), //
	      @ApiResponse(code = 404, message = "The user doesn't exist"), //
	      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
	  public String activate(@ApiParam("Username") @PathVariable String username) {
	    userService.activate(username);
	    return username;
	  }

	  @GetMapping(value = "/{username}")
	  @PreAuthorize("hasRole('ROLE_ADMIN')")
	  @ApiOperation(value = "${UserController.search}", response = UsersResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
	  @ApiResponses(value = {//
	      @ApiResponse(code = 400, message = "Something went wrong"), //
	      @ApiResponse(code = 403, message = "Access denied"), //
	      @ApiResponse(code = 404, message = "The user doesn't exist"), //
	      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
	  public UsersResponseDTO search(@ApiParam("Username") @PathVariable String username) {
	    return modelMapper.map(userService.search(username), UsersResponseDTO.class);
	  }

	  @GetMapping(value = "/me")
	  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
	  @ApiOperation(value = "${UserController.me}", response = UsersResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
	  @ApiResponses(value = {//
	      @ApiResponse(code = 400, message = "Something went wrong"), //
	      @ApiResponse(code = 403, message = "Access denied"), //
	      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
	  public UsersResponseDTO whoami(HttpServletRequest req) {
	    return modelMapper.map(userService.whoami(req), UsersResponseDTO.class);
	  }

	  @GetMapping("/refresh")
	  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
	  public String refresh(HttpServletRequest req) {
	    return userService.refresh(req.getRemoteUser());
	  }
}
