
% Jingbin Wang, Feb 1, 2002
% *** My first work using MatLab  ****
%
% Project 1: image mosaic  

function matrix=mylevmar(base_image, unregistered, matrix)
% The function below implement Levenberg-Marquardt minimization method
% 
% base_image, unregister: the pair images to be registered
% matrix: the projective transform matrix between two images%
%
% function [didx,didy]=DerivIntensity1(base_image,unregister_image,i,j,matrix) and
% function [didx,didy]=DerivIntensity2(base_image,unregister_image,n,m,matrix) are 
% two versions of implementation to
% be called to calculate the intensity gradient here
%

base_dims=size(base_image);
unregis_dims=size(unregistered);
sumerror=0;

% calculate summation of error
for j=1:unregis_dims(2)
    for i=1:unregis_dims(1)
        cppt=[j,i,1]*matrix;
        cppt=cppt(:)/cppt(3);
        m=int16(cppt(1)); n=int16(cppt(2));
        if (0<m)& (m< base_dims(2)) & (0<n) &(n<base_dims(1)) & ( int16(base_image(n,m,1))~=-1)
                % the overlapped region, calculate the e^2
                % use RGB Luminance value = 0.3 R + 0.59 G + 0.11 B as intensity
                unregisI=0.3*unregistered(i,j,1)+0.59*unregistered(i,j,2)+0.11*unregistered(i,j,3);
                baseI=0.3*base_image(n,m,1)+0.59*base_image(n,m,2)+0.11*base_image(n,m,3);
                sumerror=sumerror+(baseI-unregisI)^2;
        end
    end
end

% start the minimization process
itenum=0;
alfa=0.001;
while(itenum < 10)    
    % intialize the A matrix and b vector and alfa value at the begining of every iteration    
    Amatrix(8,8)=0;
    bvec(8)=0;
    for j=1:unregis_dims(2)
        for i=1:unregis_dims(1)
            cppt=[j,i,1]*matrix;
            cppt=cppt(:)/cppt(3);
            m=double(int16(cppt(1))); n=double(int16(cppt(2)));
            %the overlapped region
            if (0<m)& (m<base_dims(2)) & (0<n) &(n<base_dims(1)) & ((base_image(n,m,1))~=-1)
                % x <--> j, y <--> i, x' <--> m, y' <--> n
                % di'/dx' <--> didx, di'/dy' <--> didy
                unregisI=0.3*unregistered(i,j,1)+0.59*unregistered(i,j,2)+0.11*unregistered(i,j,3);
                baseI=0.3*base_image(n,m,1)+0.59*base_image(n,m,2)+0.11*base_image(n,m,3);
                errori=baseI-unregisI;
                D=cppt(3);                
                
                % calculate the dI'/dx', dI'/dy', bilinear interpolation
                % two versions of implementation
                
                % function [didx,didy]=DerivIntensity1(base_image,unregister_image,i,j,matrix);
                [didx,didy]=DerivIntensity2(base_image,unregistered,cppt(2),cppt(1),matrix);
                % calculate the de/dm(k) , k=1:8
                dedm(1)=j*didx/D;
                dedm(2)=didx*i/D;
                dedm(3)=didx/D;
                dedm(4)=didy*j/D;
                dedm(5)=didy*i/D;
                dedm(6)=didy/D;
                dedm(7)=-j*(m*didx+n*didy)/D;
                dedm(8)=-i*(m*didx+n*didy)/D;
                % accumulate the Amatrix and bvec
                for p=1:8
                    for q=1:8
                        Amatrix(p,q)=Amatrix(p,q)+dedm(p)*dedm(q);
                    end
                end
                for k=1:8
                    bvec(k)=bvec(k)-errori*dedm(k);
                end
            end
        end
    end  
    
    % update the matrix
    oldmatrix=matrix;
    deltmat=inv(Amatrix+alfa*eye(8,8))*bvec';
    for i=1:8
        matrix(i)=matrix(i)+deltmat(i);
    end
    oldsumerror=sumerror;
    sumerror=0;
    % calculate summation of error    
    for j=1:unregis_dims(2)
        for i=1:unregis_dims(1)
            cppt=[j,i,1]*matrix;
            cppt=cppt(:)/cppt(3);
            m=double(int16(cppt(1))); n=double(int16(cppt(2)));
            if (0<m)& (m<base_dims(2)) & (0<n) &(n<base_dims(1)) & ((base_image(n,m,1))~=-1)
                % the overlapped region, calculate the e^2
                % use RGB Luminance value = 0.3 R + 0.59 G + 0.11 B as intensity
                unregisI=0.3*unregistered(i,j,1)+0.59*unregistered(i,j,2)+0.11*unregistered(i,j,3);
                baseI=0.3*base_image(n,m,1)+0.59*base_image(n,m,2)+0.11*base_image(n,m,3);
                sumerror=sumerror+(baseI-unregisI)^2;
            end
        end
    end
    % if sumerror doesnt decrease, increment alfa, and update matrix
    if sumerror>oldsumerror
        alfa=2*alfa;
        matrix=oldmatrix;
        deltmat=inv(Amatrix+alfa*eye(8,8))*bvec';
        for i=1:8
            matrix(i)=matrix(i)+deltmat(i);
        end          
    end
    itenum=itenum+1;
end
resulterror=sumerror;