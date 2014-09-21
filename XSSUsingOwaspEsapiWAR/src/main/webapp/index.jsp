<!DOCTYPE html>
<html>
  <head>
    <title>Registration Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  </head>
  <body>
    <form action="portal/Registration?">
      <table border="1">
        <thead>
          <tr>
            <th></th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Name</td>
            <td><input type="text" name="name" value="" /></td>
          </tr>
          <tr>
            <td>Age</td>
            <td><input type="text" name="age" value="" /></td>
          </tr>
          <tr>
            <td>Occupation</td>
            <td>
              <select name="occupation">
                <option>Government</option>
                <option>Private</option>
                <option>Others</option>
              </select>
            </td>
          </tr>
          <tr>
            <td>Address</td>
            <td><textarea name="address" cols="30" rows="5"></textarea> </td>
          </tr>
          <tr>
            <td></td>
            <td><input type="submit"/></td>
          </tr>
        </tbody>
      </table>
    </form>
  </body>
</html>
