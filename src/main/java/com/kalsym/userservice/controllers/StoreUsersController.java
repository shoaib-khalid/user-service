package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.Auth;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.models.daos.StoreUserSession;
import com.kalsym.userservice.models.daos.StoreUser;
import com.kalsym.userservice.models.daos.StoreShiftSummary;
import com.kalsym.userservice.models.daos.StoreShiftSummaryDetails;
import com.kalsym.userservice.models.Store;
import com.kalsym.userservice.models.email.AccountVerificationEmailBody;
import com.kalsym.userservice.models.email.Email;
import com.kalsym.userservice.models.requestbodies.AuthenticationBody;
import com.kalsym.userservice.models.requestbodies.ShiftBody;
import com.kalsym.userservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.userservice.repositories.StoreUserSessionsRepository;
import com.kalsym.userservice.repositories.StoreUsersRepository;
import com.kalsym.userservice.repositories.StoreRepository;
import com.kalsym.userservice.repositories.StoreShiftSummaryRepository;
import com.kalsym.userservice.repositories.StoreShiftSummaryDetailsRepository;
import com.kalsym.userservice.services.EmaiVerificationlHandler;
import com.kalsym.userservice.services.FCMService;
import com.kalsym.userservice.utils.DateTimeUtil;
import com.kalsym.userservice.utils.Logger;
import com.kalsym.userservice.utils.Utils;
import com.kalsym.userservice.models.requestbodies.RefreshTokenRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Sarosh
 */
@RestController()
@RequestMapping("/stores/{storeId}/users")
public class StoreUsersController {
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    StoreUsersRepository storeUsersRepository;
    
    @Autowired
    StoreRepository storeRepository;

    @Autowired
    RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Autowired
    StoreUserSessionsRepository storeUserSessionsRepository;
    
    @Autowired
    StoreShiftSummaryRepository storeShiftSummaryRepository;
    
    @Autowired
    StoreShiftSummaryDetailsRepository storeShiftSummaryDetailsRepository;
    
    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    EmaiVerificationlHandler emaiVerificationlHandler;
    
    @Value("${symplified.whatsapp.service.url:http://209.58.160.20:2001}")
    private String whatsappServiceUrl;
    
    @Autowired
    FCMService fcmService;
        
    @Value("${store.staff.session.expiry:36000}")
    private int expiry;

    @GetMapping(path = {"/"}, name = "store-users-get")
    @PreAuthorize("hasAnyAuthority('store-users-get', 'all')")
    public ResponseEntity<HttpReponse> getUsers(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        StoreUser user = new StoreUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setRoleId(roleId);
        user.setLocked(locked);
        user.setStoreId(storeId);
        user.setPhoneNumber(phoneNumber);
        user.setDeactivated(false);
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, user + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<StoreUser> example = Example.of(user, matcher);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(storeUsersRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-users-get")
    @PreAuthorize("hasAnyAuthority('store-users-get', 'all')")
    public ResponseEntity<HttpReponse> getUsersById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<StoreUser> optCustomer = storeUsersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optCustomer.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-users-delete")
    @PreAuthorize("hasAnyAuthority('store-users-delete', 'all')")
    public ResponseEntity<HttpReponse> deleteUserById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<StoreUser> optCustomer = storeUsersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        StoreUser user = optCustomer.get();
        user.setDeactivated(Boolean.TRUE);
        storeUsersRepository.save(user);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "store-users-put")
    @PreAuthorize("hasAnyAuthority('store-users-put', 'all')")
    public ResponseEntity<HttpReponse> putUserById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id, @RequestBody StoreUser body) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<StoreUser> optCustomer = storeUsersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        StoreUser user = optCustomer.get();
        List<String> errors = new ArrayList<>();

        List<StoreUser> customers = storeUsersRepository.findByStoreId(storeId);

        for (StoreUser existingCustomer : customers) {
            if (!user.equals(existingCustomer)) {
                if (existingCustomer.getUsername() != null && existingCustomer.getUsername().equals(body.getUsername())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingCustomer.getEmail() != null && existingCustomer.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "email already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingCustomer.getEmail().equals(body.getEmail())) {
                        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userId already exists", "");
                        response.setStatus(HttpStatus.CONFLICT);
                        errors.add("userId already exists");
                        response.setData(errors);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    }
                }
            }

        }

        if (null != body.getPassword() && body.getPassword().length() > 0) {
            body.setPassword(bcryptEncoder.encode(body.getPassword()));
        } else {
            body.setPassword(null);
        }

        user.update(body);
        user.setUpdated(DateTimeUtil.currentTimestamp());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(storeUsersRepository.save(user));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/register", name = "store-users-post")
    @PreAuthorize("hasAnyAuthority('store-users-post', 'all')")
    public ResponseEntity<HttpReponse> postUser(HttpServletRequest request,
            @PathVariable String storeId,
            @Valid @RequestBody StoreUser body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        List<String> errors = new ArrayList<>();
        
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        if (!storeOpt.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Store not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Store store = storeOpt.get();
        
        //String username = store.getStorePrefix()+body.getUsername();
        String username = body.getUsername();
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Username to register:"+username);
        StoreUser existingUser = storeUsersRepository.findByUsernameAndStoreId(username, storeId);
        if (existingUser!=null) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
            response.setStatus(HttpStatus.CONFLICT);
            errors.add("username already exists");
            response.setData(errors);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        body.setUsername(username);
        body.setPassword(bcryptEncoder.encode(body.getPassword()));        
        body.setCreated(DateTimeUtil.currentTimestamp());
        body.setUpdated(DateTimeUtil.currentTimestamp());
        body.setLocked(false);
        body.setDeactivated(false);
        body.setRoleId("STORE_WAITER");
        
        body = storeUsersRepository.save(body);
        body.setPassword(null);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user created with id: " + body.getId(), "");
        
        //send password in Whatsapp
        //String[] recipients = {body.getPhoneNumber()};
        //String[] message = {body.getPhoneNumber(), password};
        //Utils.sendWhatsappMessage(whatsappServiceUrl, recipients, message);
        
        response.setStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "store-users-authenticate")
    public ResponseEntity authenticateUser(@Valid @RequestBody AuthenticationBody body,
            @PathVariable String storeId,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "Authenticate username:"+body.getUsername());

        Authentication auth = null;
        StoreUser user = null;
        try {

            user = storeUsersRepository.findByUsername(body.getUsername());
            if (user==null) {
                Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username not valid");
                response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        
            if (user.getDeactivated()) {
                Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "User is deactivated");
                response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            boolean match = bcryptEncoder.matches(body.getPassword(), user.getPassword());

            if (!match) {
                Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user password not valid");
                response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (BadCredentialsException e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error validating user", "", e);
            response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error validating user", "", e);
            response.setStatus(HttpStatus.UNAUTHORIZED, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user authenticated", "");

//        Customer user = customersRepository.findByUsernameOrEmail(body.getUsername(), body.getUsername());
        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(user.getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {

            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }

        StoreUserSession session = new StoreUserSession();
        session.setRemoteAddress(request.getRemoteAddr());
        session.setOwnerId(user.getId());
        session.setUsername(user.getUsername());
        session.setCreated(DateTimeUtil.currentTimestamp());
        session.setUpdated(DateTimeUtil.currentTimestamp());
        session.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        session.setStatus("ACTIVE");
        session.generateTokens();

        session = storeUserSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(user.getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "generated token", "");

        //update fcm token
        if (body.getFcmToken()!=null) {
            user.setFcmToken(body.getFcmToken());
            storeUsersRepository.save(user);
        }
        
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    @PutMapping(path = {"/refreshFcmToken/{id}"}, name = "store-users-put")
    @PreAuthorize("hasAnyAuthority('store-users-put', 'all')")
    public ResponseEntity<HttpReponse> refreshFcmToken(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id, @RequestBody RefreshTokenRequest req) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "FcmToken:"+req.getFcmToken(), "");

        Optional<StoreUser> optCustomer = storeUsersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        StoreUser user = optCustomer.get();
        
        user.setFcmToken(req.getFcmToken());            
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user fcm token updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(storeUsersRepository.save(user));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    
    //authentication
    @PostMapping(path = "/endShift", name = "store-users-put")
    @PreAuthorize("hasAnyAuthority('store-users-put', 'all')")
    public ResponseEntity endShift(@Valid @RequestBody ShiftBody body,
            @PathVariable String storeId,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        if (!storeOpt.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Store not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Store store = storeOpt.get();
        StoreShiftSummary summaryData = new StoreShiftSummary();
        String staffId = body.getUserId();
        
        //get first order
        List<Object[]> firstOrder = storeShiftSummaryRepository.getFirstOrder(staffId);
        if (firstOrder.size()>0) {
            Object[] orderDetails = firstOrder.get(0);
            //create shift summary data
            summaryData.setUserId(staffId);
            summaryData.setFirstOrderId((String)orderDetails[0]);
            Timestamp ts = (java.sql.Timestamp)orderDetails[1];
            summaryData.setFirstOrderTime(ts);
            
            //get last order
            List<Object[]> lastOrder = storeShiftSummaryRepository.getLastOrder(staffId);
            if (lastOrder.size()>0) {
                Object[] lastOrderDetails = lastOrder.get(0);
                summaryData.setLastOrderId((String)lastOrderDetails[0]);
                Timestamp lastTs = (java.sql.Timestamp)lastOrderDetails[1];
                summaryData.setLastOrderTime(lastTs);
            }
            
            summaryData = storeShiftSummaryRepository.save(summaryData);

            //calculate total sales for current user        
            List<Object[]> itemList = storeShiftSummaryRepository.getOrderSummary(staffId);
            List<StoreShiftSummaryDetails> summaryDetailsList = new ArrayList();
            for (int i=0;i<itemList.size();i++) {
                Object[] order = itemList.get(i);
                BigDecimal totalSales = (BigDecimal)order[0];
                String paymentChannel = (String)order[1];            
                //insert shift summary details
                StoreShiftSummaryDetails summaryDetails = new StoreShiftSummaryDetails();
                summaryDetails.setSummaryId(summaryData.getId());
                summaryDetails.setSaleAmount(totalSales.doubleValue());
                summaryDetails.setPaymentChannel(paymentChannel);
                summaryDetails = storeShiftSummaryDetailsRepository.save(summaryDetails);
                summaryDetailsList.add(summaryDetails);
            }
            summaryData.setSummaryDetails(summaryDetailsList);
            
            //close order
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Close order");
            storeShiftSummaryRepository.UpdateOrderClose(staffId);
        }
        
        //send push notification to staff app
        Optional<StoreUser> optUser = storeUsersRepository.findById(staffId);
        if (!optUser.isPresent()) {
            String staffFcmToken = optUser.get().getFcmToken();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Sending FCM to staffId:"+staffId+" token:"+staffFcmToken);                    
            fcmService.sendLogoutNotification(staffId, staffFcmToken, storeId, store.getDomain());                
        }
       
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "summaryData:"+summaryData, "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(summaryData);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI();

        Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "validation failed", "");
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        HttpReponse response = new HttpReponse(request.getRequestURI());
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
     
}
